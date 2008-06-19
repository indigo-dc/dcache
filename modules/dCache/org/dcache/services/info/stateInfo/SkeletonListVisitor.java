package org.dcache.services.info.stateInfo;

import java.util.Map;

import org.apache.log4j.Logger;
import org.dcache.services.info.base.BooleanStateValue;
import org.dcache.services.info.base.FloatingPointStateValue;
import org.dcache.services.info.base.IntegerStateValue;
import org.dcache.services.info.base.StatePath;
import org.dcache.services.info.base.StateVisitor;
import org.dcache.services.info.base.StringStateValue;

/**
 * A simple skeleton class that provides a StateVisitor that iterates over items in a list.
 * The constructor should be called with the StatePath of the parent StateComposite.  Each
 * child of this StateComposite is considered a item within the list and newListItem() will be
 * called for each such list.  Subclasses may overload that method.
 * 
 * The method getKey() will return the last item, as recorded by the subclass calling
 * super.newListItem( key), typically done within an overloaded method newListitem. 
 * 
 * @author Paul Millar <paul.millar@desy.de>
 */
public class SkeletonListVisitor implements StateVisitor {

	private static Logger _log = Logger.getLogger( SkeletonListVisitor.class);

	final private StatePath _pathToList;
	
	/** The key of the current branch */
	private String _thisKey;

	/**
	 * Instantiate the list over the items underneath pathToList.
	 * @param pathToList the StatePath representing the parent object for this list.
	 */
	protected SkeletonListVisitor( StatePath pathToList) {
		if( _log.isDebugEnabled())
			_log.debug( "Searching on path " + pathToList);

		_pathToList = pathToList;
	}
	
	/**
	 * The super-Class should override one of the following four methods
	 */
	public void visitBoolean(StatePath path, BooleanStateValue value) {}
	public void visitFloatingPoint(StatePath path, FloatingPointStateValue value) {}
	public void visitInteger(StatePath path, IntegerStateValue value) {}
	public void visitString(StatePath path, StringStateValue value) {}

	public void visitCompositePreSkipDescend(StatePath path, Map<String, String> metadata) {}
	public void visitCompositePostSkipDescend(StatePath path, Map<String, String> metadata) {}
	public void visitCompositePreLastDescend(StatePath path, Map<String, String> metadata) {}

	public void visitCompositePreDescend(StatePath path, Map<String, String> metadata) {
		if( _pathToList.isParentOf( path)) {
			if( _log.isDebugEnabled())
				_log.debug( "Entering " + path);
			
			newListItem( path.getLastElement());
		}
	}

	public void visitCompositePostDescend(StatePath path, Map<String, String> metadata) {
		if( _pathToList.isParentOf( path)) {
			if( _log.isDebugEnabled())
				_log.debug( "Leaving " + path);
			
			exitingListItem( path.getLastElement());
		}
	}
	
	/**
	 * Method called whenever a new list item is visited.
	 * @param listItemName the name of the list item to record.
	 * @see the getKey() method.
	 */
	protected void newListItem( String listItemName) {
		if( _log.isDebugEnabled())
			_log.debug( "Assigning _thisKey to " + listItemName);

		_thisKey = listItemName;
	}
	
	/**
	 * Method called whenever the visitor is leaving a list item.
	 * @param listItemName the name of the list item that is being left.
	 */
	protected void exitingListItem( String listItemName) {
		if( _log.isDebugEnabled())
			_log.debug( "Resetting _thisKey to null on leaving " + listItemName);
		
		_thisKey = null;
	}
	
	/**
	 * Obtain the StatePath to the parent object for all list items. 
	 */
	protected StatePath getPathToList() {
		return _pathToList;
	}
	
	/**
	 * @return the name of the last item in the list, or null if not currently within a list item.
	 */
	protected String getKey() {
		return _thisKey;
	}
}
