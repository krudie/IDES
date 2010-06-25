package pluggable.layout.tree;

import ides.api.model.fsa.FSAModel;
import ides.api.model.fsa.FSAState;
import ides.api.model.fsa.FSATransition;
import ides.api.plugin.layout.FSALayouter;
import ides.api.plugin.operation.CheckingToolbox;

import java.awt.geom.Point2D;
import java.awt.geom.Point2D.Float;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;
import java.util.Set;

public class TreeLayouter implements FSALayouter
{
	public String getName()
	{
		return "Simple tree layout";
	}

	public Map<Long, Float> layout(FSAModel model)
	{

		LinkedList<FSAState> currentList = new LinkedList<FSAState>();
		LinkedList<FSAState> futureList = new LinkedList<FSAState>();
		LinkedList<FSAState> processList = new LinkedList<FSAState>();
		float xCoordinate = 0;
		float yCoordinate = 0;
		float displacementFactorForY = 150;
		float displacementFactorForX = 150;
		float yPosition = 0;
		FSAModel modelToLayout = model;
		FSAState innerStates;
		FSAState stateExtracted;
		int stateCounter = 0;
		float yTop = 0;
		int numberOfNodes = 0;

		Set<Long> initialStateIds = CheckingToolbox
				.getInitialStates(modelToLayout);

		stateExtracted = modelToLayout
				.getState((Long)initialStateIds.toArray()[0]);
		currentList.add(stateExtracted);

		futureList.clear();

		Map<Long, Point2D.Float> ret = new HashMap<Long, Point2D.Float>();

		do
		{
			stateCounter = 0;
			xCoordinate = xCoordinate + displacementFactorForX;
			yPosition = 0;
			// numberOfNodes = 0;
			numberOfNodes = currentList.size();
			while (!currentList.isEmpty())
			{

				stateExtracted = currentList.poll();
				if (!processList.contains(stateExtracted))
				{

					yTop = -((numberOfNodes - 1) * (displacementFactorForY / 2));
					yPosition = yTop
							+ ((stateCounter) * displacementFactorForY);
					ret.put(stateExtracted.getId(), new Point2D.Float(
							xCoordinate,
							yPosition));
					processList.add(stateExtracted);
					stateCounter++;
				}

				for (Iterator<FSATransition> i = stateExtracted
						.getOutgoingTransitionsListIterator(); i.hasNext();)
				{
					innerStates = i.next().getTarget();
					if (!processList.contains(innerStates)
							&& !futureList.contains(innerStates))
					{

						futureList.add(innerStates);

					}
				}

			}
			currentList = futureList;
			futureList = new LinkedList<FSAState>();

		}
		while (!currentList.isEmpty());
		return ret;
	}
}
