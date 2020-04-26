# File Format for Supervisory Event Set Models


This file format follows that defined for Model Representation in "IDES 2.1 File Format".

The model field uses version 3 and type "SupEventSet". Unlike an FSA model, the data field
contains only one internal field `<event>`, which is described below
(adapted from "IDES 2.1 File Format").

The event tag `<event>` contains the property `id` that internally identifies the event.
There are two mandatory tags for `<event>`: `properties` and `name`.
There can be many `event` tags in a FSA model file but all `ids` have to be unique.

For the properties tag `<properties>` there are currently two supported properties
for an event: `<controllable />` and `<observable />`.
The possible fields describing the properties of an event are:

1. A controllable event:
```
    <properties>
        <controllable/>
    </properties>
```
2. An observable event
```
    <properties>
        <observable/>
    </properties>
```
3. A controllable and observable event.
```
    <properties>
        <controllable/>
        <observable/>
    </properties>
```
4. A non-controllable and non-observable event
```
    <properties/>
```

The name tag `<name>` contains the name of the event.

There are no new meta fields associated with this model.

A sample file appears as follows:
```
<?xml version="1.0" encoding="UTF-8"?>
<model version="3" type="SupEventSet" id="MyEventSet">
<data>
	<event id="2">
		<properties>
			<controllable />
		</properties>
		<name>b</name>
	</event>
	<event id="3">
		<properties />
		<name>c</name>
	</event>
	<event id="1">
		<properties>
			<controllable />
			<observable />
		</properties>
		<name>a</name>
	</event>
</data>
</model>
```
