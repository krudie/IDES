package ides.api.latex;

import java.awt.Color;
import java.awt.Container;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.MediaTracker;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.awt.image.Raster;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import javax.imageio.ImageIO;

/**
 * This class allows for rendering of LaTeX documents from within Java. It can
 * be used also for the coversion of LaTeX documents into EPS files (only the
 * first page is output).
 * <p>
 * The external software that has to be available is:
 * <ul>
 * <li><b>latex</b>
 * <li><b>dvips</b> (found in the same folder as latex)
 * <li><b>GhostScript</b> (preferrably version 8+)
 * </ul>
 * <p>
 * To use the renderer:
 * 
 * <pre>
 * Renderer r = Renderer.getRenderer(&quot;path to latex&quot;, &quot;path to ghostscript&quot;);
 * 
 * Image i = r.renderString(&quot;$\\alpha$&quot;);
 * </pre>
 * 
 * @author Lenko Grigorov, Michael Wood
 * @version 2.1
 */
public class Renderer {

    /**
     * The time-out when calling external programs.
     */
    public final static int TIMEOUT = 15000; // 15 sec.

    /**
     * The resolution to be used when rendering LaTeX files
     */
    public final static int SCREEN_RESOLUTION = 96; // dpi

    /**
     * The path to latex+dvips
     */
    protected java.io.File latexPath;

    /**
     * The path to ghostscript
     */
    protected java.io.File gsPath;

    /**
     * Used to call external programs from a separate thread so that timeout can be
     * implemented.
     * 
     * @author Lenko Grigorov
     * @see Renderer#execute(Renderer.Executor)
     */
    private class Executor extends Thread {
        /**
         * array with the command+parameters
         */
        private String[] cmd;

        /**
         * startup directory
         */
        private File dir;

        /**
         * indicate whether call completed
         */
        private boolean done = false;

        /**
         * Construct an Executor for the given command and startup directory.
         * 
         * @param cmd the command+parameters that will be called
         * @param dir the startup directory for the new process
         */
        public Executor(String[] cmd, File dir) {
            this.cmd = cmd;
            this.dir = dir;
        }

        /**
         * Executes the call as specified during the construction of this object. After
         * the call completes, <code>done</code> is set to true and all threads waiting
         * on this Executor are awoken. The thread can be interrupted (via
         * {@link java.lang.Thread#interrupt()}) in case of a timeout; if this happens,
         * the call is aborted.
         */
        @Override
        public void run() {
            Runtime rt = Runtime.getRuntime();
            try {
                Process proc = rt.exec(cmd, null, dir);
                try {
                    proc.waitFor();
                } catch (InterruptedException e) {
                    proc.destroy();
                }
                synchronized (this) {
                    done = true;
                    notifyAll();
                }
            } catch (IOException e) {
            }
        }

        /**
         * Returns <code>true</code> if the the execution of the call has completed and
         * <code>false</code> otherwise.
         * 
         * @return <code>true</code> if the the execution of the call has completed,
         *         <code>false</code> otherwise.
         */
        public boolean isDone() {
            return done;
        }

        /**
         * Returns the command (without parameters) wich this object will execute. This
         * can be used in announcements in case of timeouts.
         * 
         * @return command (without parameters) wich this object will execute
         * @see Renderer#execute(Renderer.Executor)
         */
        public String getCommand() {
            return cmd[0];
        }
    }

    /**
     * Construct a new object which will use the provided paths to
     * <code>latex</code> and GhostScript.
     * 
     * @param latexPath the directory where the <code>latex</code> and
     *                  <code>dvips</code> binaries are located (e.g.,
     *                  "c:\texmf\miktex\bin")
     * @param gsPath    the location of the GhostScript command-line engine (e.g.,
     *                  "c:\gs\gs8.14\bin\gswin32c.exe" on Windows)
     */
    private Renderer(java.io.File latexPath, java.io.File gsPath) {
        this.latexPath = latexPath;
        this.gsPath = gsPath;
    }

    /**
     * Create a new <code>Renderer</code> which will use the provided paths when
     * calling LaTeX, dvips and GhostScript.
     * 
     * @param latexPath the path to <code>latex</code> and <code>dvips</code> (e.g.,
     *                  "c:\texmf\miktex\bin")
     * @param gsPath    the path to <code>gswin32c</code> (e.g., "c:\gs\gs8.14\bin")
     * @return a new <code>Renderer</code> which will use the provided paths when
     *         calling LaTeX, dvips and GhostScript.
     */
    public static Renderer getRenderer(java.io.File latexPath, java.io.File gsPath) {
        return new Renderer(latexPath, gsPath);
    }

    /**
     * Render a piece of LaTeX code into a PNG image file.
     * 
     * @param s LaTeX code (the code goes in the body of a LaTeX document)
     * @param f file where to save image. If the file exists, its content will be
     *          overwritten.
     * @return the image which was saved in the file
     * @throws IOException          if there was a problem with the IO
     * @throws LatexRenderException if there was a problem while rendering the LaTeX
     *                              code
     * @see #renderString(String, OutputStream)
     * @see #renderString(String, String, File)
     * @see #renderString(String, String, OutputStream)
     * @see #renderDocument(File, File)
     * @see #renderDocument(File, OutputStream)
     * @see #renderDocument(InputStream, File)
     * @see #renderDocument(InputStream, OutputStream)
     * @see #renderDocument(String, File)
     * @see #renderDocument(String, OutputStream)
     * @see #renderString(String)
     * @see #renderString(String, String)
     * @see #renderDocument(File)
     * @see #renderDocument(InputStream)
     * @see #renderDocument(String)
     */
    public BufferedImage renderString(String s, File f) throws IOException, LatexRenderException {
        return renderString(s, "", f);
    }

    /**
     * Render a piece of LaTeX code into a PNG image and output to a stream.
     * 
     * @param s   LaTeX code (the code goes in the body of a LaTeX document)
     * @param out stream where to output image. The stream will not be closed.
     * @return the image which was saved in the file
     * @throws IOException          if there was a problem with the IO
     * @throws LatexRenderException if there was a problem while rendering the LaTeX
     *                              code
     * @see #renderString(String, File)
     * @see #renderString(String, String, File)
     * @see #renderString(String, String, OutputStream)
     * @see #renderDocument(File, File)
     * @see #renderDocument(File, OutputStream)
     * @see #renderDocument(InputStream, File)
     * @see #renderDocument(InputStream, OutputStream)
     * @see #renderDocument(String, File)
     * @see #renderDocument(String, OutputStream)
     * @see #renderString(String)
     * @see #renderString(String, String)
     * @see #renderDocument(File)
     * @see #renderDocument(InputStream)
     * @see #renderDocument(String)
     */
    public BufferedImage renderString(String s, OutputStream out) throws IOException, LatexRenderException {
        return renderString(s, "", out);
    }

    /**
     * Render a piece of LaTeX code into a PNG image file.
     * 
     * @param s      LaTeX code (the code goes in the body of a LaTeX document)
     * @param header LaTeX environment setup, such as <code>usepackage</code> (the
     *               code goes before the body of a LaTeX document)
     * @param f      file where to save image. If the file exists, its content will
     *               be overwritten.
     * @return the image which was saved in the file
     * @throws IOException          if there was a problem with the IO
     * @throws LatexRenderException if there was a problem while rendering the LaTeX
     *                              code
     * @see #renderString(String, File)
     * @see #renderString(String, OutputStream)
     * @see #renderString(String, String, OutputStream)
     * @see #renderDocument(File, File)
     * @see #renderDocument(File, OutputStream)
     * @see #renderDocument(InputStream, File)
     * @see #renderDocument(InputStream, OutputStream)
     * @see #renderDocument(String, File)
     * @see #renderDocument(String, OutputStream)
     * @see #renderString(String)
     * @see #renderString(String, String)
     * @see #renderDocument(File)
     * @see #renderDocument(InputStream)
     * @see #renderDocument(String)
     */
    public BufferedImage renderString(String s, String header, File f) throws IOException, LatexRenderException {
        return renderDocument("\\documentclass{article}\n" + header
                + "\n\\pagestyle{empty}\n\\setlength{\\parindent}{0pt}\n\\begin{document}\n" + s + "\n\\end{document}",
                f);
    }

    /**
     * Render a piece of LaTeX code into a PNG image and output to a stream.
     * 
     * @param s      LaTeX code (the code goes in the body of a LaTeX document)
     * @param header LaTeX environment setup, such as <code>usepackage</code> (the
     *               code goes before the body of a LaTeX document)
     * @param out    stream where to output image. The stream will not be closed.
     * @return the image which was saved in the file
     * @throws IOException          if there was a problem with the IO
     * @throws LatexRenderException if there was a problem while rendering the LaTeX
     *                              code
     * @see #renderString(String, File)
     * @see #renderString(String, OutputStream)
     * @see #renderString(String, String, File)
     * @see #renderDocument(File, File)
     * @see #renderDocument(File, OutputStream)
     * @see #renderDocument(InputStream, File)
     * @see #renderDocument(InputStream, OutputStream)
     * @see #renderDocument(String, File)
     * @see #renderDocument(String, OutputStream)
     * @see #renderString(String)
     * @see #renderString(String, String)
     * @see #renderDocument(File)
     * @see #renderDocument(InputStream)
     * @see #renderDocument(String)
     */
    public BufferedImage renderString(String s, String header, OutputStream out)
            throws IOException, LatexRenderException {
        return renderDocument("\\documentclass{article}\n" + header
                + "\n\\pagestyle{empty}\n\\setlength{\\parindent}{0pt}\n\\begin{document}\n" + s + "\n\\end{document}",
                out);
    }

    /**
     * Render the first page of a LaTeX document into a PNG image file.
     * 
     * @param doc LaTeX document
     * @param f   file where to save image. If the file exists, its content will be
     *            overwritten.
     * @return the image which was saved in the file
     * @throws IOException          if there was a problem with the IO
     * @throws LatexRenderException if there was a problem while rendering the LaTeX
     *                              code
     * @see #renderString(String, File)
     * @see #renderString(String, OutputStream)
     * @see #renderString(String, String, File)
     * @see #renderString(String, String, OutputStream)
     * @see #renderDocument(File, File)
     * @see #renderDocument(File, OutputStream)
     * @see #renderDocument(InputStream, File)
     * @see #renderDocument(InputStream, OutputStream)
     * @see #renderDocument(String, OutputStream)
     * @see #renderString(String)
     * @see #renderString(String, String)
     * @see #renderDocument(File)
     * @see #renderDocument(InputStream)
     * @see #renderDocument(String)
     */
    public BufferedImage renderDocument(String doc, File f) throws IOException, LatexRenderException {
        BufferedImage im = renderDocument(doc);
        ImageIO.write(im, "png", f);
        return im;
    }

    /**
     * Render the first page of a LaTeX document into a PNG image and output to a
     * stream.
     * 
     * @param doc LaTeX document
     * @param out stream where to output image. The stream will not be closed.
     * @return the image which was saved in the file
     * @throws IOException          if there was a problem with the IO
     * @throws LatexRenderException if there was a problem while rendering the LaTeX
     *                              code
     * @see #renderString(String, File)
     * @see #renderString(String, OutputStream)
     * @see #renderString(String, String, File)
     * @see #renderString(String, String, OutputStream)
     * @see #renderDocument(File, File)
     * @see #renderDocument(File, OutputStream)
     * @see #renderDocument(InputStream, File)
     * @see #renderDocument(InputStream, OutputStream)
     * @see #renderDocument(String, File)
     * @see #renderString(String)
     * @see #renderString(String, String)
     * @see #renderDocument(File)
     * @see #renderDocument(InputStream)
     * @see #renderDocument(String)
     */
    public BufferedImage renderDocument(String doc, OutputStream out) throws IOException, LatexRenderException {
        BufferedImage im = renderDocument(doc);
        ImageIO.write(im, "png", out);
        return im;
    }

    /**
     * Render the first page of a LaTeX document into a PNG image file.
     * 
     * @param doc file containing the LaTeX document
     * @param f   file where to save image. If the file exists, its content will be
     *            overwritten.
     * @return the image which was saved in the file
     * @throws IOException          if there was a problem with the IO
     * @throws LatexRenderException if there was a problem while rendering the LaTeX
     *                              code
     * @see #renderString(String, File)
     * @see #renderString(String, OutputStream)
     * @see #renderString(String, String, File)
     * @see #renderString(String, String, OutputStream)
     * @see #renderDocument(File, OutputStream)
     * @see #renderDocument(InputStream, File)
     * @see #renderDocument(InputStream, OutputStream)
     * @see #renderDocument(String, File)
     * @see #renderDocument(String, OutputStream)
     * @see #renderString(String)
     * @see #renderString(String, String)
     * @see #renderDocument(File)
     * @see #renderDocument(InputStream)
     * @see #renderDocument(String)
     */
    public BufferedImage renderDocument(File doc, File f) throws IOException, LatexRenderException {
        return renderDocument(new FileInputStream(doc), f);
    }

    /**
     * Render the first page of a LaTeX document into a PNG image and output to a
     * stream.
     * 
     * @param doc file containing the LaTeX document
     * @param out stream where to output image. The stream will not be closed.
     * @return the image which was saved in the file
     * @throws IOException          if there was a problem with the IO
     * @throws LatexRenderException if there was a problem while rendering the LaTeX
     *                              code
     * @see #renderString(String, File)
     * @see #renderString(String, OutputStream)
     * @see #renderString(String, String, File)
     * @see #renderString(String, String, OutputStream)
     * @see #renderDocument(File, File)
     * @see #renderDocument(InputStream, File)
     * @see #renderDocument(InputStream, OutputStream)
     * @see #renderDocument(String, File)
     * @see #renderDocument(String, OutputStream)
     * @see #renderString(String)
     * @see #renderString(String, String)
     * @see #renderDocument(File)
     * @see #renderDocument(InputStream)
     * @see #renderDocument(String)
     */
    public BufferedImage renderDocument(File doc, OutputStream out) throws IOException, LatexRenderException {
        return renderDocument(new FileInputStream(doc), out);
    }

    /**
     * Render the first page of a LaTeX document into a PNG image file.
     * 
     * @param doc InputStream from which the LaTeX document will be read. The stream
     *            is closed after reading the document.
     * @param f   file where to save image. If the file exists, its content will be
     *            overwritten.
     * @return the image which was saved in the file
     * @throws IOException          if there was a problem with the IO
     * @throws LatexRenderException if there was a problem while rendering the LaTeX
     *                              code
     * @see #renderString(String, File)
     * @see #renderString(String, OutputStream)
     * @see #renderString(String, String, File)
     * @see #renderString(String, String, OutputStream)
     * @see #renderDocument(File, File)
     * @see #renderDocument(File, OutputStream)
     * @see #renderDocument(InputStream, OutputStream)
     * @see #renderDocument(String, File)
     * @see #renderDocument(String, OutputStream)
     * @see #renderString(String)
     * @see #renderString(String, String)
     * @see #renderDocument(File)
     * @see #renderDocument(InputStream)
     * @see #renderDocument(String)
     */
    public BufferedImage renderDocument(InputStream doc, File f) throws IOException, LatexRenderException {
        BufferedImage im = renderDocument(doc);
        ImageIO.write(im, "png", f);
        return im;
    }

    /**
     * Render the first page of a LaTeX document into a PNG image and output to a
     * stream.
     * 
     * @param doc InputStream from which the LaTeX document will be read. The stream
     *            is closed after reading the document.
     * @param out stream where to output image. The stream will not be closed.
     * @return the image which was saved in the file
     * @throws IOException          if there was a problem with the IO
     * @throws LatexRenderException if there was a problem while rendering the LaTeX
     *                              code
     * @see #renderString(String, File)
     * @see #renderString(String, OutputStream)
     * @see #renderString(String, String, File)
     * @see #renderString(String, String, OutputStream)
     * @see #renderDocument(File, File)
     * @see #renderDocument(File, OutputStream)
     * @see #renderDocument(InputStream, File)
     * @see #renderDocument(String, File)
     * @see #renderDocument(String, OutputStream)
     * @see #renderString(String)
     * @see #renderString(String, String)
     * @see #renderDocument(File)
     * @see #renderDocument(InputStream)
     * @see #renderDocument(String)
     */
    public BufferedImage renderDocument(InputStream doc, OutputStream out) throws IOException, LatexRenderException {
        BufferedImage im = renderDocument(doc);
        ImageIO.write(im, "png", out);
        return im;
    }

    /**
     * Render a piece of LaTeX code.
     * 
     * @param s LaTeX code (the code goes in the body of a LaTeX document)
     * @return the image with the rendition
     * @throws IOException          if there was a problem with the IO
     * @throws LatexRenderException if there was a problem while rendering the LaTeX
     *                              code
     * @see #renderString(String, File)
     * @see #renderString(String, OutputStream)
     * @see #renderString(String, String, File)
     * @see #renderString(String, String, OutputStream)
     * @see #renderDocument(File, File)
     * @see #renderDocument(File, OutputStream)
     * @see #renderDocument(InputStream, File)
     * @see #renderDocument(InputStream, OutputStream)
     * @see #renderDocument(String, File)
     * @see #renderDocument(String, OutputStream)
     * @see #renderString(String, String)
     * @see #renderDocument(File)
     * @see #renderDocument(InputStream)
     * @see #renderDocument(String)
     */
    public BufferedImage renderString(String s) throws IOException, LatexRenderException {
        return renderString(s, "");
    }

    /**
     * Render a piece of LaTeX code.
     * 
     * @param s      LaTeX code (the code goes in the body of a LaTeX document)
     * @param header LaTeX environment setup, such as <code>usepackage</code> (the
     *               code goes before the body of a LaTeX document)
     * @return the image with the rendition
     * @throws IOException          if there was a problem with the IO
     * @throws LatexRenderException if there was a problem while rendering the LaTeX
     *                              code
     * @see #renderString(String, File)
     * @see #renderString(String, OutputStream)
     * @see #renderString(String, String, File)
     * @see #renderString(String, String, OutputStream)
     * @see #renderDocument(File, File)
     * @see #renderDocument(File, OutputStream)
     * @see #renderDocument(InputStream, File)
     * @see #renderDocument(InputStream, OutputStream)
     * @see #renderDocument(String, File)
     * @see #renderDocument(String, OutputStream)
     * @see #renderString(String)
     * @see #renderDocument(File)
     * @see #renderDocument(InputStream)
     * @see #renderDocument(String)
     */
    public BufferedImage renderString(String s, String header) throws IOException, LatexRenderException {
        return renderDocument("\\documentclass{article}\n" + header
                + "\n\\pagestyle{empty}\n\\setlength{\\parindent}{0pt}\n\\begin{document}\n" + s + "\n\\end{document}");
    }

    /**
     * Render the first page of a LaTeX document.
     * 
     * @param doc LaTeX document
     * @return the image with the rendition
     * @throws IOException          if there was a problem with the IO
     * @throws LatexRenderException if there was a problem while rendering the LaTeX
     *                              code
     * @see #renderString(String, File)
     * @see #renderString(String, OutputStream)
     * @see #renderString(String, String, File)
     * @see #renderString(String, String, OutputStream)
     * @see #renderDocument(File, File)
     * @see #renderDocument(File, OutputStream)
     * @see #renderDocument(InputStream, File)
     * @see #renderDocument(InputStream, OutputStream)
     * @see #renderDocument(String, File)
     * @see #renderDocument(String, OutputStream)
     * @see #renderString(String)
     * @see #renderString(String, String)
     * @see #renderDocument(File)
     * @see #renderDocument(InputStream)
     */
    public BufferedImage renderDocument(String doc) throws IOException, LatexRenderException {
        File latexFile = File.createTempFile("ides", ".tex");
        FileWriter out = new FileWriter(latexFile);
        out.write(doc);
        out.close();
        return renderTempFile(latexFile);
    }

    /**
     * Render the first page of a LaTeX document.
     * 
     * @param doc file containing the LaTeX document
     * @return the image with the rendition
     * @throws IOException          if there was a problem with the IO
     * @throws LatexRenderException if there was a problem while rendering the LaTeX
     *                              code
     * @see #renderString(String, File)
     * @see #renderString(String, OutputStream)
     * @see #renderString(String, String, File)
     * @see #renderString(String, String, OutputStream)
     * @see #renderDocument(File, File)
     * @see #renderDocument(File, OutputStream)
     * @see #renderDocument(InputStream, File)
     * @see #renderDocument(InputStream, OutputStream)
     * @see #renderDocument(String, File)
     * @see #renderDocument(String, OutputStream)
     * @see #renderString(String)
     * @see #renderString(String, String)
     * @see #renderDocument(InputStream)
     * @see #renderDocument(String)
     */
    public BufferedImage renderDocument(File doc) throws IOException, LatexRenderException {
        return renderDocument(new FileInputStream(doc));
    }

    /**
     * Render the first page of a LaTeX document.
     * 
     * @param doc InputStream from which the LaTeX document will be read. The stream
     *            is closed after reading the document.
     * @return the image with the rendition
     * @throws IOException          if there was a problem with the IO
     * @throws LatexRenderException if there was a problem while rendering the LaTeX
     *                              code
     * @see #renderString(String, File)
     * @see #renderString(String, OutputStream)
     * @see #renderString(String, String, File)
     * @see #renderString(String, String, OutputStream)
     * @see #renderDocument(File, File)
     * @see #renderDocument(File, OutputStream)
     * @see #renderDocument(InputStream, File)
     * @see #renderDocument(InputStream, OutputStream)
     * @see #renderDocument(String, File)
     * @see #renderDocument(String, OutputStream)
     * @see #renderString(String)
     * @see #renderString(String, String)
     * @see #renderDocument(File)
     * @see #renderDocument(String)
     */
    public BufferedImage renderDocument(InputStream doc) throws IOException, LatexRenderException {
        File latexFile = File.createTempFile("ides", ".tex");
        BufferedOutputStream out = new BufferedOutputStream(new FileOutputStream(latexFile));
        BufferedInputStream in = new BufferedInputStream(doc);
        int b;
        while ((b = in.read()) >= 0) {
            out.write(b);
        }
        in.close();
        out.close();
        return renderTempFile(latexFile);
    }

    /**
     * Returns an empty image (1x1 pixel, white).
     * 
     * @return a 1x1 pixel white image
     */
    public static BufferedImage getEmptyImage() {
        BufferedImage empty = new BufferedImage(1, 1, BufferedImage.TYPE_BYTE_GRAY);
        Graphics g = empty.getGraphics();
        g.setColor(Color.WHITE);
        g.drawLine(0, 0, 0, 0);
        return empty;
    }

    /**
     * Render the first page of the LaTeX document contained in a temporary file.
     * This method is called by all other rendering methods. The method removes all
     * temporary files (including the file in the parameter) after finishing the
     * rendering.
     * 
     * @param latexFile file containing the LaTeX document
     * @return image with the rendition
     * @throws IOException          if there was a problem with the IO
     * @throws LatexRenderException if there was a problem while rendering the LaTeX
     *                              code
     * @see #renderString(String, File)
     * @see #renderString(String, OutputStream)
     * @see #renderString(String, String, File)
     * @see #renderString(String, String, OutputStream)
     * @see #renderDocument(File, File)
     * @see #renderDocument(File, OutputStream)
     * @see #renderDocument(InputStream, File)
     * @see #renderDocument(InputStream, OutputStream)
     * @see #renderDocument(String, File)
     * @see #renderDocument(String, OutputStream)
     * @see #renderString(String)
     * @see #renderString(String, String)
     * @see #renderDocument(File)
     * @see #renderDocument(InputStream)
     * @see #renderDocument(String)
     */
    protected BufferedImage renderTempFile(File latexFile) throws IOException, LatexRenderException {
        BufferedImage im = null;
        try {
            im = latex2PNG(latexFile, SCREEN_RESOLUTION);
            Rectangle r = findBoundingBox(im.getData());
            // crop
            im = im.getSubimage(r.x, r.y, r.width + 1, r.height + 1);
        } finally
        // remove temporary files
        {
            removeTempFiles(latexFile);
        }
        return im;
    }

    /**
     * Renders the first page of the LaTeX document contained in a file into an
     * uncropped image.
     * 
     * @param latexFile file containing the LaTeX document
     * @param dpi       the resolution to be used for the redering (in dots per
     *                  inch)
     * @return uncropped image of the first page of the LaTeX document
     * @throws IOException          if there was a problem with the IO
     * @throws LatexRenderException if there was a problem while rendering the LaTeX
     *                              code
     * @see #renderTempFile(File)
     * @see #tempLatex2EPS(File, File)
     */
    protected BufferedImage latex2PNG(File latexFile, int dpi) throws IOException, LatexRenderException {
        String[] command;

        // LaTeX
        command = new String[3];
        command[0] = latexPath.getCanonicalPath() + File.separator + "latex";
        command[1] = "-interaction=batchmode";
        command[2] = latexFile.getName();
        Executor latex = new Executor(command, new File(latexFile.getParentFile().getCanonicalPath()));
        execute(latex);

        // Check if LaTeX output anything
        File latexOutputFile = new File(latexFile.getParentFile().getCanonicalPath() + File.separator
                + latexFile.getName().substring(0, latexFile.getName().lastIndexOf('.')) + ".dvi");
        if (!latexOutputFile.exists()) {
            return getEmptyImage();
        }

        // DVIPS
        command = new String[6];
        command[0] = latexPath.getCanonicalPath() + File.separator + "dvips";
        command[1] = "-E";
        command[2] = "-q";
        command[3] = "-o";
        command[4] = latexFile.getName().substring(0, latexFile.getName().lastIndexOf('.')) + ".ps";
        command[5] = latexFile.getName().substring(0, latexFile.getName().lastIndexOf('.')) + ".dvi";
        Executor dvips = new Executor(command, new File(latexFile.getParentFile().getCanonicalPath()));
        execute(dvips);

        // GhostScript
        command = new String[9];
        command[0] = gsPath.getCanonicalPath();
        command[1] = "-dBATCH";
        command[2] = "-dNOPAUSE";
        command[3] = "-dTextAlphaBits=2";
        command[4] = "-dGraphicsAlphaBits=2";
        command[5] = "-sDEVICE=pnggray";
        command[6] = "-r" + dpi;
        command[7] = "-sOutputFile=" + latexFile.getName().substring(0, latexFile.getName().lastIndexOf('.')) + ".png";
        command[8] = latexFile.getName().substring(0, latexFile.getName().lastIndexOf('.')) + ".ps";
        Executor gs = new Executor(command, new File(latexFile.getParentFile().getCanonicalPath()));
        execute(gs);

        File pngFile = new File(
                latexFile.getCanonicalPath().substring(0, latexFile.getCanonicalPath().lastIndexOf('.')) + ".png");

        // if createImage() directly from file, file cannot be deleted
        byte[] pngData = new byte[(int) pngFile.length()];
        FileInputStream in = new FileInputStream(pngFile);
        if (in.read(pngData, 0, pngData.length) != pngData.length) {
            throw new IOException("Unable to completely read intermediate image file.");
        }
        in.close();

        Toolkit awtTK = Toolkit.getDefaultToolkit();
        Image tempIm = awtTK.createImage(pngData);
        MediaTracker mediaTracker = new MediaTracker(new Container());
        mediaTracker.addImage(tempIm, 0);
        try {
            mediaTracker.waitForID(0);
        } catch (InterruptedException e) {
        }
        BufferedImage im = new BufferedImage(tempIm.getWidth(null), tempIm.getHeight(null),
                BufferedImage.TYPE_BYTE_GRAY);
        im.createGraphics().drawImage(tempIm, 0, 0, null);
        return im;
    }

    /**
     * Deletes all files which are located in the same directory as the parameter
     * and which have the same name (only the extension can differ).
     * <p>
     * For example, if the parameter is the file "c:\temp\ides0013.tex", then the
     * files "c:\temp\ides0013.*" will be deleted.
     * 
     * @param latexFile file whose name and location will be used to determine which
     *                  files to delete
     * @see #renderTempFile(File)
     * @see #tempLatex2EPS(File, File)
     */
    private void removeTempFiles(File latexFile) {
        String[] files = latexFile.getParentFile().list();
        for (int i = 0; i < files.length; ++i) {
            if (files[i].lastIndexOf('.') < 0) {
                continue;
            }
            if (files[i].substring(0, files[i].lastIndexOf('.'))
                    .equals(latexFile.getName().substring(0, latexFile.getName().lastIndexOf('.')))) {
                try {
                    new File(latexFile.getParentFile().getCanonicalPath() + File.separator + files[i]).delete();
                } catch (IOException e) {
                }
            }
        }
    }

    /**
     * Execute a call to an external program as per an {@link Renderer.Executor}
     * object, using a timeout of {@link #TIMEOUT} milliseconds.
     * 
     * @param ex the {@link Renderer.Executor} with the call that has to be executed
     * @throws LatexRenderException if the call timed out or if the call didn't
     *                              succeed
     * @see Renderer.Executor
     */
    private void execute(Executor ex) throws LatexRenderException {
        ex.start();
        synchronized (ex) {
            if (!ex.isDone()) {
                try {
                    ex.wait(TIMEOUT);
                    if (!ex.isDone()) {
                        ex.interrupt();
                        throw new LatexRenderException("The invocation of " + ex.getCommand() + " timed out.");
                    }
                } catch (InterruptedException e) {
                }
            }
        }
    }

    /**
     * Finds the bounding box of an image with a single band (grayscale). The
     * bounding box is defined as the smallest rectangle such that all pixels
     * outside the rectangle are white.
     * 
     * @param r raster of the image
     * @return the bounding box of the image
     * @see #renderTempFile(File)
     * @see #tempLatex2EPS(File, File)
     */
    private Rectangle findBoundingBox(Raster r) {
        int w = r.getWidth();
        int h = r.getHeight();
        int minX = Integer.MAX_VALUE;
        int maxX = Integer.MIN_VALUE;
        int minY = Integer.MAX_VALUE;
        int maxY = Integer.MIN_VALUE;
        boolean emptyImage = true;
        for (int i = 0; i < w; ++i) {
            for (int j = 0; j < h; ++j) {
                int pixel = r.getSample(i, j, 0);
                if (pixel != 255) {
                    if (i < minX) {
                        minX = i;
                    }
                    if (i > maxX) {
                        maxX = i;
                    }
                    if (j < minY) {
                        minY = j;
                    }
                    if (j > maxY) {
                        maxY = j;
                    }
                    emptyImage = false;
                }
            }
        }
        if (emptyImage) {
            return new Rectangle(0, 0, 0, 0);
        }
        if (maxX - minX < 0) {
            minX = 0;
            maxX = 0;
        }
        if (maxY - minY < 0) {
            minY = 0;
            maxY = 0;
        }
        return new Rectangle(minX, minY, maxX - minX, maxY - minY);
    }

    /**
     * Convert the first page of a LaTeX document into an EPS file.
     * 
     * @param doc    LaTeX document
     * @param target file where to save the EPS. If the file exists, its content
     *               will be overwritten.
     * @throws IOException          if there was a problem with the IO
     * @throws LatexRenderException if there was a problem while rendering the LaTeX
     *                              code
     * @see #latex2EPS(File, File)
     * @see #latex2EPS(InputStream, File)
     */
    public void latex2EPS(String doc, File target) throws IOException, LatexRenderException {
        File latexFile = File.createTempFile("ides", ".tex");
        FileWriter out = new FileWriter(latexFile);
        out.write(doc);
        out.close();
        tempLatex2EPS(latexFile, target);
    }

    /**
     * Convert the first page of a LaTeX document into an EPS file.
     * 
     * @param doc    file containing the LaTeX document
     * @param target file where to save the EPS. If the file exists, its content
     *               will be overwritten.
     * @throws IOException          if there was a problem with the IO
     * @throws LatexRenderException if there was a problem while rendering the LaTeX
     *                              code
     * @see #latex2EPS(InputStream, File)
     * @see #latex2EPS(String, File)
     */
    public void latex2EPS(File doc, File target) throws IOException, LatexRenderException {
        latex2EPS(new FileInputStream(doc), target);
    }

    /**
     * Convert the first page of a LaTeX document into an EPS file.
     * 
     * @param doc    InputStream from which the LaTeX document will be read. The
     *               stream is closed after reading the document.
     * @param target file where to save the EPS. If the file exists, its content
     *               will be overwritten.
     * @throws IOException          if there was a problem with the IO
     * @throws LatexRenderException if there was a problem while rendering the LaTeX
     *                              code
     * @see #latex2EPS(File, File)
     * @see #latex2EPS(String, File)
     */
    public void latex2EPS(InputStream doc, File target) throws IOException, LatexRenderException {
        File latexFile = File.createTempFile("ides", ".tex");
        BufferedOutputStream out = new BufferedOutputStream(new FileOutputStream(latexFile));
        BufferedInputStream in = new BufferedInputStream(doc);
        int b;
        while ((b = in.read()) >= 0) {
            out.write(b);
        }
        in.close();
        out.close();
        tempLatex2EPS(latexFile, target);
    }

    /**
     * Convert the first page of a LaTeX document contained in a temporary file into
     * an EPS file. This method is called by all other methods that convert into
     * EPS. The method removes all temporary files (including the file in the
     * parameter) after finishing conversion.
     * 
     * @param latexFile file containing the LaTeX document
     * @param target    file where to save the EPS. If the file exists, its content
     *                  will be overwritten.
     * @throws IOException          if there was a problem with the IO
     * @throws LatexRenderException if there was a problem while rendering the LaTeX
     *                              code
     * @see #latex2EPS(File, File)
     * @see #latex2EPS(InputStream, File)
     * @see #latex2EPS(String, File)
     */
    protected void tempLatex2EPS(File latexFile, File target) throws IOException, LatexRenderException {
        BufferedImage im = null;
        try {
            im = latex2PNG(latexFile, 72); // bounding boxes are computed using
            // 72dpi metrics
            Rectangle r = findBoundingBox(im.getData());
            r.height = r.height + 1;
            r.width = r.width + 1;
            r.y = im.getHeight() - r.y - r.height; // y=0 is at the bottom of
            // the page in EPS
            fixBoundingBox(r, new File(
                    latexFile.getCanonicalPath().substring(0, latexFile.getCanonicalPath().lastIndexOf('.')) + ".ps"),
                    target);
        } finally
        // remove temporary files
        {
            removeTempFiles(latexFile);
        }
    }

    /**
     * Fixes the <code>BoundingBox</code> comment in EPS files so it reflects the
     * true boundary of the graphic. Some tools (notably <code>dvips</code>) may not
     * always generate a correct <code>BoundingBox</code> comment.
     * 
     * @param boundary the correct boundary of the graphic
     * @param source   EPS file which needs to be fixed. Cannot be the same as the
     *                 target file.
     * @param target   file where the corrected EPS will be written. Cannot be the
     *                 same as the source file.
     * @throws IOException if there was a problem with the IO
     * @see #tempLatex2EPS(File, File)
     */
    protected void fixBoundingBox(Rectangle boundary, File source, File target) throws IOException {
        BufferedInputStream in = new BufferedInputStream(new FileInputStream(source));
        BufferedOutputStream out = new BufferedOutputStream(new FileOutputStream(target));
        int b;
        while ((b = in.read()) >= 0) // loop to find "%%BoundingBox:"
        {
            out.write(b);
            if (b == 10) // if new line
            {
                char[] boundingBoxString = "%%BoundingBox:".toCharArray();
                final int BBSTRING_LENGTH = boundingBoxString.length;
                boolean exitLoop = false;
                int[] buffer = null;
                while (!exitLoop) // loop is repeated if new line is
                // encountered
                {
                    exitLoop = true;
                    buffer = new int[BBSTRING_LENGTH];
                    for (int i = 0; i < BBSTRING_LENGTH; ++i) // read string
                    // to match
                    {
                        buffer[i] = in.read();
                        if (buffer[i] == 10) // new line was encountered
                        {
                            for (int j = 0; j <= i; ++j) {
                                out.write(buffer[j]);
                            }
                            exitLoop = false;
                            break;
                        } else if (buffer[i] < 0) {
                            break;
                        }
                    }
                }
                boolean matched = true;
                for (int i = 0; i < BBSTRING_LENGTH; ++i) {
                    if (boundingBoxString[i] != buffer[i]) {
                        matched = false;
                        break;
                    }
                }
                if (!matched) // write buffer to output
                {
                    for (int i = 0; i < BBSTRING_LENGTH; ++i) {
                        if (buffer[i] >= 0) {
                            out.write(buffer[i]);
                        } else {
                            break;
                        }
                    }
                } else
                // string was matched
                {
                    while ((b = in.read()) != 10) {
                        ;
                    }
                    out.write(("%%BoundingBox: " + boundary.x + " " + boundary.y + " " + (boundary.x + boundary.width)
                            + " " + (boundary.y + boundary.height) + (char) 10).getBytes());
                    break; // stop looking for BoundingBox comment
                }
            }
        }
        while ((b = in.read()) >= 0) {
            out.write(b);
        }
        in.close();
        out.close();
    }

    /*
     * here just for reference public static void main(String[] args) throws
     * Exception { Renderer t=Renderer.getRenderer(new File("c:\\program
     * files\\texmf\\miktex\\bin"),new File("C:\\Program
     * Files\\gs\\gs8.14\\bin")); Image i=t.renderString("$|\\Sigma|$");
     * t.renderStringToFile("$|\\Sigma|$",new File("c:\\!work\\foo.png"));
     * t.latex2EPS(new File("c:\\!work\\graph1a.tex"),new
     * File("c:\\!work\\graph1b.eps")); javax.swing.JFrame win=new
     * javax.swing.JFrame(); javax.swing.ImageIcon icon=new
     * javax.swing.ImageIcon(i); javax.swing.JButton p=new
     * javax.swing.JButton(icon); win.getContentPane().add(p);
     * win.setDefaultCloseOperation(javax.swing.JFrame.EXIT_ON_CLOSE); win.pack();
     * win.setVisible(true); }
     */
}
