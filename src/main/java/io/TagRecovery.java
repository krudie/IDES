package io;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import ides.api.core.Hub;

public class TagRecovery {
    protected String dataType = null;

    protected String dataVersion = "";

    protected long dataOffset = 0;

    protected long dataLength = 0;

    protected List<String> tags = new LinkedList<String>();

    protected Map<String, String> tagsVersions = new HashMap<String, String>();

    protected Map<String, Long> tagsOffsets = new HashMap<String, Long>();

    protected Map<String, Long> tagsLengths = new HashMap<String, Long>();

    public String getDataType() {
        return dataType;
    }

    public String getDataVersion() {
        return dataVersion;
    }

    public long getDataOffset() {
        return dataOffset;
    }

    public long getDataLength() {
        return dataLength;
    }

    public List<String> getTags() {
        return tags;
    }

    public String getTagVersion(String tag) {
        return tagsVersions.get(tag);
    }

    public long getTagOffset(String tag) {
        return tagsOffsets.get(tag);
    }

    public long getTagLength(String tag) {
        return tagsLengths.get(tag);
    }

    private static final int MODEL_INFO_S = 0;

    private static final int MODEL_INFO_E = 1;

    private static final int DATA_S = 2;

    private static final int DATA_E = 4;

    private static final int META_S = 8;

    private static final int META_SE = 16;

    private static final int META_E = 32;

    protected static final String MODEL = "<model ";

    protected static final String MODEL_CLOSE = "</model>";

    protected static final String DATA = "<data>";

    protected static final String DATA_CLOSE = "</data>";

    protected static final String META = "<meta ";

    protected static final String META_SIMPLE = "<meta>";

    protected static final String META_CLOSE = "</meta>";

    protected static final String CLOSE = ">";

    protected static final String TYPE = " type=\"";

    protected static final String VERSION = " version=\"";

    protected static final String TAG = " tag=\"";

    public void parse(InputStream stream) throws IOException {
        tags.clear();
        tagsLengths.clear();
        tagsOffsets.clear();
        tagsVersions.clear();

        long counter = 0;
        StringBuffer buffer = new StringBuffer();
        for (int i = 0; i < 10; ++i) {
            buffer.append((char) stream.read());
        }

        StringBuffer element = null;
        int nestedElements = 0;
        String currentTag = "";
        String currentVersion = "";
        long currentOffset = 0;
        long currentLength = 0;

        int mode = MODEL_INFO_S;
        while (buffer.length() > 0) {
            switch (mode) {
            case MODEL_INFO_S:
                if (buffer.length() >= MODEL.length()
                        && MODEL.equals(buffer.substring(0, MODEL.length()).toLowerCase())) {
                    element = new StringBuffer();
                    element.append(buffer.charAt(0));
                    mode = MODEL_INFO_E;
                }
                break;
            case MODEL_INFO_E:
                if (buffer.length() >= CLOSE.length() && CLOSE.equals(buffer.substring(0, CLOSE.length()))) {
                    element.append(buffer.substring(0, CLOSE.length()));
                    parseModel(element.toString());
                    mode = DATA_S;
                } else {
                    element.append(buffer.charAt(0));
                }
                break;
            case DATA_S:
                if (buffer.length() >= DATA.length() && DATA.equals(buffer.substring(0, DATA.length()).toLowerCase())) {
                    dataOffset = counter + DATA.length();
                    mode = DATA_E;
                }
                break;
            case DATA_E:
                if (buffer.length() >= DATA_CLOSE.length()
                        && DATA_CLOSE.equals(buffer.substring(0, DATA_CLOSE.length()).toLowerCase())) {
                    if (nestedElements == 0) {
                        dataLength = counter - dataOffset;
                        mode = META_S;
                    } else {
                        nestedElements--;
                    }
                } else if (buffer.length() >= DATA.length()
                        && DATA.equals(buffer.substring(0, DATA.length()).toLowerCase())) {
                    nestedElements++;
                }
                break;
            case META_S:
                if (buffer.length() >= META.length() && META.equals(buffer.substring(0, META.length()).toLowerCase())) {
                    element = new StringBuffer();
                    element.append(buffer.charAt(0));
                    mode = META_SE;
                }
                break;
            case META_SE:
                if (buffer.length() >= CLOSE.length() && CLOSE.equals(buffer.substring(0, CLOSE.length()))) {
                    element.append(buffer.substring(0, CLOSE.length()));
                    String[] info = parseMeta(element.toString());
                    currentTag = info[0];
                    currentVersion = info[1];
                    currentOffset = counter + CLOSE.length();
                    mode = META_E;
                } else {
                    element.append(buffer.charAt(0));
                }
                break;
            case META_E:
                if (buffer.length() >= META_CLOSE.length()
                        && META_CLOSE.equals(buffer.substring(0, META_CLOSE.length()).toLowerCase())) {
                    if (nestedElements == 0) {
                        currentLength = counter - currentOffset;
                        tags.add(currentTag);
                        tagsVersions.put(currentTag, currentVersion);
                        tagsOffsets.put(currentTag, currentOffset);
                        tagsLengths.put(currentTag, currentLength);
                        mode = META_S;
                    } else {
                        nestedElements--;
                    }
                } else if ((buffer.length() >= META.length()
                        && META.equals(buffer.substring(0, META.length()).toLowerCase()))
                        || (buffer.length() >= META_SIMPLE.length()
                                && META_SIMPLE.equals(buffer.substring(0, META_SIMPLE.length()).toLowerCase()))) {
                    nestedElements++;
                }
                break;
            }
            // advance
            buffer.deleteCharAt(0);
            ++counter;
            int r = stream.read();
            if (r >= 0) {
                buffer.append((char) r);
            }
        }
    }

    protected void parseModel(String element) throws IOException {
        int idx;
        idx = element.toLowerCase().indexOf(TYPE);
        if (idx >= 0) {
            String type = element.substring(idx + TYPE.length());
            idx = type.indexOf("\"");
            if (idx >= 0) {
                type = type.substring(0, idx);
            }
            dataType = type;
        } else {
            throw new IOException(Hub.string("ioMissingAttribute"));
        }
        idx = element.toLowerCase().indexOf(VERSION);
        if (idx >= 0) {
            String ver = element.substring(idx + VERSION.length());
            idx = ver.indexOf("\"");
            if (idx >= 0) {
                ver = ver.substring(0, idx);
            }
            dataVersion = ver;
        } else {
            throw new IOException(Hub.string("ioMissingAttribute"));
        }
    }

    protected String[] parseMeta(String element) throws IOException {
        String[] ret = new String[] { "", "" };
        int idx;
        idx = element.toLowerCase().indexOf(TAG);
        if (idx >= 0) {
            String tag = element.substring(idx + TAG.length());
            idx = tag.indexOf("\"");
            if (idx >= 0) {
                tag = tag.substring(0, idx);
            }
            ret[0] = tag;
        } else {
            throw new IOException(Hub.string("ioMissingAttribute"));
        }
        idx = element.toLowerCase().indexOf(VERSION);
        if (idx >= 0) {
            String ver = element.substring(idx + VERSION.length());
            idx = ver.indexOf("\"");
            if (idx >= 0) {
                ver = ver.substring(0, idx);
            }
            ret[1] = ver;
        } else {
            throw new IOException(Hub.string("ioMissingAttribute"));
        }
        return ret;
    }
}
