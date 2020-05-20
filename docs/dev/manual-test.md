# Manual test protocol for IDES

*Original authors: Helen Bretzke and Sarah-Jane Whittaker*

## Requirements

- There must be a sequence for every major feature
- The sequences which are not expected to finish almost instantaneously must be noted as such
- Every sequence must be as efficient as possible
- The test workspace must possess the following:
    - more than one model
	- one empty model
	- one extremely large "stress" model which:
        * must be easily examined
        * should possess scannable clusters of nodes and edges in simple shapes
    - at least one small but realistic model (e.g., mutex)
    - a primary test model with at least one each of the following:
        * initial and final states
        * states with and without labels
        * self-loops, straight edges, curves and looped edges
        * controllable/uncontrollable and/or observable/unobservable events named according to their properties
        * an edge for every single event
        * an edge for every combination of events with different properties
        * an unlabeled edge
        * a combination of LaTeX and non-LaTeX labels for both states and events
        * a state with multiple self-loops
        * a pair of states with multiple directed edges between the same source and destination

## Startup

1. Start IDES
    1. The current model must be empty
    2. The current model must be named "Untitled"
    3. The current model must not be labeled dirty (with an asterisk) in the filmstrip
2. Select the **Create** tool
3. Perform a single left-click on the worksheet to add a state to the current model
    1. The current model must be labeled dirty in the filmstrip
4. Open the test workspace
5. Do not save the current model (select "No")
    1. The "Untitled" model must disappear
    2. The test workspace must appear
    3. All models must not be labeled dirty in the filmstrip
    4. The thumbnail for each model must be properly displayed in the filmstrip
    5. All models must be "selectable" in the filmstrip
    6. All models must display properly in the worksheet
6. Close IDES
    1. The model and workspace must disappear without querying to save
    2. IDES must terminate cleanly

## Features

### LaTeX Rendering and Uniform Node Sizing

1. Start IDES
2. Open the test workspace, followed by the primary test model
    1. The test workspace must appear as it did in the previous test
3. Ensure LaTeX rendering is disabled
    1. All labels must appear in their original text
4. Ensure uniform node sizing is disabled
    1. Every node must only be slightly larger than its label
5. Enable LaTeX rendering
    1. All labels must appear in LaTeX-rendered form
    2. All nodes must have re-sized to contain the new label
6. Enable uniform node sizing
    1. Every node must be slightly larger than the largest label
    2. All labels must still appear in LaTeX-rendered form
7. Disable LaTeX rendering
    1. All labels must appear in their original text
    2. All nodes must have re-sized to slightly larger than the largest label
8. Disable uniform node sizing
    1. All nodes must have re-sized to slightly larger than its own label
    2. All labels must still appear in their original text

### Zoom

1. Ensure the primary test model is open and in focus
2. Using the **Zoom** tool, select 50%
3. Zoom in to 200%
4. Zoom back to 100%
    1. The model should appear as it did before the test sequence

    NOTE: Further Zoom testing should and will be done in conjunction with other tests
    such as adding and removing elements to ensure that changes to the model do not
    adversely affect this tool.
5. Close IDES
    1. There should be no prompt to save unless you inadvertently clicked on something
    2. The model and workspace must disappear
    3. IDES must terminate cleanly

## Creating Nodes

1. Start IDES
2. Open the test workspace, followed by the empty test model
    1. The changes made in the previous test must not be present
3. Ensure that LaTeX rendering is disabled
4. Select the **Create** tool
5. Perform a single left-click on the worksheet
    1. A new, unlabeled node must appear under the icon
6. Double-click on the new node
    1. The *Node label* dialog must appear
7. Enter the label "x" in the dialog and press "Enter"
    1. The label "x" must appear in the center of the node
8. Right-click on node "x" and select "Initial" from the menu
    1. An initial arrow must appear on the edge of the node
9. Perform another single left-click on the worksheet to create a new node
10. Right-click on the new node and select "Label" from the menu
    1. The *Node label* dialog must appear
11. Enter the label "yz" in the dialog and click outside of the dialog on the worksheet
    1. The label "yz" must appear in the center of the node
    2. A new, unlabeled node must appear where clicked
12. Perform another single left-click to create a new node
13. Move the cursor over the new node, hold the left mouse button down, drag the
    cursor to the edge of the screen and release

    NOTE: The intent is to create a node that is half on and half off the worksheet,
    thus forcing a resize
    
    IMPORTANT: If you pull the cursor outside of the IDES canvas, it will cause the
    current edge to disappear; this is planned behaviour
    
    1. A new, unlabeled node must appear at the end drag point, partly off the screen
    2. A new, unlabeled edge must connect the start node to the end node
    3. The drawing area must be resized with arrow(s) for scrolling
14. Double-click on the start node or right-click and select "Label"
15. Enter the string "q" in the "Node label" dialog and press "ESC"
    1. The node must remain unlabeled
16. Label the start node "$\\alpha$"
17. Label the end node "$\\xi(x, ConObs) = yz$"
18. Right-click on node "$\\xi(x, ConObs) = yz$" and select "Marked" from the menu
    1. The node must now be drawn with a double line instead of a single thick edge
19. Perform another single left-click to create a new node
20. Move the cursor over the new node, hold the left mouse button down, drag the
    cursor to another part of the worksheet and release
21. Label the start node "1" and denote it as marked
22. Label the end node "Ninety-nine"
23. Perform another single left-click to create a new node

    NOTE: This node will retain its default label
24. Close IDES
    1. Save the current model (select "Yes")
    2. Save the current workspace (select "Yes")
    3. The model and workspace must disappear
    4. IDES must terminate cleanly

## Creating Edges

1. Start IDES
2. Open the test workspace, followed by the previously empty test model
    1. The changes made in the previous test must be present
3. Select the **Create** tool if it is not already selected
4. Move the icon over node "x", hold the left mouse button down, drag the icon over node "yz" and release
    1. A new, unlabeled edge must appear between "x" and "yz"
5. Repeat the process outlined in the previous item
    1. Two separate, unlabeled edges must now appear between "x" and "yz"
6. Create a new node with a single left-click
7. Move the cursor over the new node, hold the left mouse button down, drag to elsewhere on the worksheet and release
8. Label the start node "3.2.1" and the end node "!@\#$%"
9. Move the icon over node "!@\#$%", hold the left mouse button down, drag the icon over node "3.2.1" and release
    1. Two unlabeled edges must appear between "!@\#$%" and "3.2.1"
    2. One edge must have "!@\#$%" as its source and "3.2.1" as its destination
    3. The other edge must have "3.2.1" as its source and "!@\#$%" as its destination
10. Move the cursor over node "Ninety-nine"
11. Perform a single left-click, followed by another

    NOTE: Two single clicks are required, not a double-click

    1. An unlabeled self loop must appear on node "Ninety-nine"
12. Create a new node by with a single left-click
13. Label this node "Me, Myself \\\\ and I" and make it marked
14. Move the cursor over the new node and perform two left clicks to create a self-loop
15. Right-click over the node and select "Add self-loop" from the menu
    1. Two unlabeled self-loops must now appear on node "Me, Myself \\\\ and I"
    2. The self-loops and edges on "Me, Myself \\\\ and I" must have adjusted their positions to fit
16. Create a new edge from "x" to "$\\alpha$"
17. Create a new edge from "yz" to "$\\xi(x, ConObs) = yz$"
18. Create a new edge from "$\\alpha$" to "1"
19. Create a new edge from "1" to "$\\xi(x, ConObs) = yz$"
20. Create a new edge from "1" to "3.2.1"
21. Create a new edge from "1" to "x"
22. Create a new edge from "3.2.1" to "$\\alpha$"
23. Create a new edge from "!@\#$%" to "Ninety-nine"
    1. There must now be nine nodes and 17 edges
24. Close IDES
    1. Save the current model (select "Yes")
    2. Save the current workspace (select "Yes")
    3. The model and workspace must disappear
    4. IDES must terminate cleanly

## Creating Events

1. Start IDES
2. Open the test workspace, followed by the previously empty test model
    1. The changes made in the previous test must be present
3. Select the **Select** tool if it is not already selected
4. Double-click on an edge from "x" to "yz"
    1. The *Assign Events to Edge* dialog must appear
    2. The *Assign Events to Edge* dialog must not contain any events
    3. The "Assign New" button must be disabled
5. Enter "ConObs" in the "Enter event to assign" text box
    1. The "Assign New" button must be enabled
    2. At no time should "Assign New" change to "Assign"
    3. The frame surrounding this area of the dialog must appear darker
6. Check both the "Controllable" and "Observable" checkboxes and select "Assign New"
    1. The event must appear in the "Assigned to Edge" list
7. Select "OK" in the dialog
    1. The dialog must disappear
    2. The event "ConObs" must be assigned to the edge
    3. The edge must remain a solid line
8. Select the *Events* tab at the top of the worksheet
    1. The event "ConObs" must be present in the list
    2. The event "ConObs" must be shown as controllable and observable
    3. The "Add" button must be disabled
    4. The "Controllable" and "Observable" checkboxes must be disabled
9. Enter the word "Con" in the *Add New Event* text field
    1. The event "ConObs" must be highlighted in the list
10. Finish the word "ConUnobs" in the text field
    1. The event "ConObs" must no longer be highlighted in the list
    2. The "Add" button must be enabled
    3. The "Controllable" and "Observable" checkboxes must be enabled
    4. The frame surrounding this area of the dialog must appear darker
11. Check both the "Controllable" and "Observable" checkboxes and select "Add"
    1. The event "ConUnobs" must appear in the list
    2. The event "ConUnobs" must be shown as controllable and observable
    3. The *Add New Event* text field must again be empty
    4. The "Add" button must be disabled
    5. The frame surrounding this area of the dialog must appear lighter
12. Double-click on "ConUnobs" in the list
    1. The event name "ConUnobs" must now be editable
13. Change the name to "UnconUnobs" in the list and press "Enter"
    1. The event must now be named "UnconUnobs" in the list
14. Uncheck both the "Controllable" and "Observable" checkboxes beside "UnconUnobs"
    1. The event must now be shown as uncontrollable and unobservable
15. Enter the word "ConUnobs" in the *Add New Event* text field
    1. While typing "Con", the event "ConObs" must be highlighted in the list
    2. After "ConU", no event must be highlighted in the list
16. Ensure the *Controllable* checkbox is checked and the *Observable* checkbox is not
17. Return the cursor to the text field and press "Enter"
    1. The event "ConUnobs" must be present in the list
    2. The event "ConUnobs" must be shown as controllable and unobservable
18. Select the *Graph* tab at the top of the worksheet
19. Select the edge from "1" to "3.2.1"
20. Right-click on the edge and select "Label with events" from the pop-up menu
    1. The *Assign Events to Edge* dialog must appear
    2. The events "ConObs’, "UnconUnobs" and "ConUnobs" must appear in the "Available" list
    3. No events must appear in the "Assigned to Edge" list
    4. The "Controllable" and "Observable" checkboxes must be checked
21. Enter "UnconObs" in the "Enter event to assign" text box
    1. While typing "Uncon", it must be "completed" in the text box with a highlighted "Unobs"
    2. The event "UnconUnobs" must be highlighted in the "Available" list
    3. The button must change from "Assign New" to "Assign" and be enabled
    4. The "Controllable" and "Observable" checkboxes must be disabled
    5. After "UnconO", no more text must appear in the text box
    6. No event must be highlighted in the list
    7. The button must change back to "Assign New"
    8. The "Controllable" and "Observable" checkboxes must now be enabled
22. Uncheck the "Controllable" checkbox (but leave "Observable" checked)
23. Return focus to the text box and press "Enter"
    1. The event "UnconObs" must appear in the "Assigned to Edge" list
24. Select "OK"
    1. The dialog must disappear
    2. The event "UnconObs" must be assigned to the edge
    3. The edge must consist of a dashed line
25. Double-click on the same edge from "1" to "3.2.1"
    1. The *Events* dialog must appear
    2. The events "ConObs’, "UnconUnobs" and "ConUnobs" must appear in the "Available" list
    3. The event "UnconObs" must appear in the "Assigned to Edge" list
    4. The "Controllable" checkbox must be unchecked
    5. The "Observable" checkbox must be checked
26. Select the "OK" button
    1. These must be no changes to the selected edge and its label
27. Again, Double-click on the same edge from "1" to "3.2.1"
28. Enter "Cancel" in the "Enter event to assign" text box
29. Check both the "Controllable" and "Observable" checkboxes
30. Click the "Assign New" button
31. Press "Cancel"
    1. The dialog must disappear
    2. No event other than "UnconObs" must be assigned to the edge
32. Double-click on an edge from "x" to "yz"
    1. The event "Cancel" must not be present
33. Enter "ESC" in the "Enter event to assign" text box
34. Uncheck both the "Controllable" and "Observable" checkboxes
35. Return focus to the text box and press "Enter"
36. Now return focus to the text box and press "ESC"
    1. The dialog must disappear
    2. No new event must be assigned to the edge
37. Select the *Events* tab at the top of the worksheet
    1. The *Controllable* checkbox must be checked and the *Observable* checkbox must not
    2. The events "Cancel" and "ESC" must not be present
38. Close IDES
    1. Save the current model (select "Yes")
    2. Save the current workspace (select "Yes")
    3. The model and workspace must disappear
    4. IDES must terminate cleanly

## Labeling

1. Ensure the previously empty test model is open and in focus
2. Select the **Select tool** if it is not already selected
3. Select the remaining unlabeled edge from "x" to "yz" and assign "UnconObs" and "UnconUnobs" to the edge
    1. The label "UnconObs, UnconUnobs" must appear on the edge
    2. The edge must consist of a dashed line
4. Select the unlabeled edge from "yz" to "$\\xi(x, ConObs) = yz$" and assign "ConObs" and "UnconObs" to the edge
    1. The label "ConObs, UnconObs" must appear on the edge
    2. The edge must consist of a dashed line
5. Select the unlabeled edge from "$\\alpha$" to "$\\xi(x, ConObs) = yz$" and assign "ConObs", "UnconObs" and "UnconUnobs" to the edge
    1. The label "ConObs, UnconObs, UnconUnobs" must appear on the edge
    2. The edge must consist of a dashed line
6. Select the unlabeled edge from "$\\alpha$" to "1" and assign "ConObs", "ConUnobs" and "UnconUnobs" to the edge
    1. The label "ConObs, ConUnobs, UnconUnobs" must appear on the edge
    2. The edge must consist of a dashed line
7. Select the unlabeled edge from "1" to "x" and assign "ConUnobs", "UnconObs" and "UnconUnobs" to the edge
    1. The label "ConUnobs, UnconObs, UnconUnobs" must appear on the edge
    2. The edge must consist of a dashed line
8. Select the unlabeled edge from "1" to "$\\xi(x, ConObs) = yz$" and assign "UnconUnobs" to the edge
    1. The label "UnconUnobs" must appear on the edge
    2. The edge must consist of a dashed line
9. Select the unlabeled edge from "1" to "Ninety-nine" and assign "ConObs", "ConUnobs" and "UnconObs" to the edge
    1. The label "ConObs, ConUnobs, UnconObs" must appear on the edge
    2. The edge must consist of a dashed line
10. Select the unlabeled edge from "3.2.1" to "$\\alpha$" and assign "ConObs" and "UnconObs" to the edge
    1. The label "ConObs, UnconObs" must appear on the edge
    2. The edge must consist of a dashed line
11. Select the unlabeled edge from "3.2.1" to "!@\#$%ˆ" and assign "ConObs" and "UnconUnobs" to the edge
    1. The label "ConObs, UnconUnobs" must appear on the edge
    2. The edge must consist of a dashed line
12. Select the unlabeled edge from "!@\#$%ˆ" to "3.2.1" and assign "ConObs", "ConUnobs", "UnconObs" and "UnconUnobs" to the edge
    1. The label "ConObs, ConUnobs, UnconObs, UnconUnobs" must appear on the edge
    2. The edge must consist of a dashed line
13. Select the unlabeled edge from "!@\#$%ˆ" to "Ninety-nine" and assign "ConObs" and "ConUnobs" to the edge
    1. The label "ConObs, ConUnobs" must appear on the edge
    2. The edge must consist of a solid line
14. Select the unlabeled self-loop on "Ninety-nine" and assign "ConUnobs" and "UnconUnobs" to the edge
    1. The label "ConUnobs, UnconUnobs" must appear on the edge
    2. The edge must consist of a dashed line
15. Select an unlabeled self-loop on "Me, Myself\\\\and I" and assign "ConUnobs" to the edge
    1. The label "ConUnobs" must appear on the edge
    2. The edge must consist of a solid line
