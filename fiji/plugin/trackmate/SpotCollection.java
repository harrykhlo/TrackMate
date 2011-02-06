package fiji.plugin.trackmate;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;

/**
 * A utility class that wrap the {@link TreeMap} we use to store the spots contained
 * in each frame with a few utility methods.
 * @author Jean-Yves Tinevez <jeanyves.tinevez@gmail.com> - Feb 6, 2011
 *
 */
public class SpotCollection implements Iterable<Spot>,  SortedMap<Integer, List<Spot>>  {

	/** The frame by frame list of spot this object wrap. */
	private TreeMap<Integer, List<Spot>> content;

	/*
	 * CONSTRUCTORS
	 */
	
	/**
	 * Construct a new SpotCollection by wrapping the given {@link TreeMap} (and using its 
	 * comparator, if any).
	 */
	public SpotCollection(TreeMap<Integer, List<Spot>> content) {
		this.content = content;

	}
	
	/**
	 * Construct a new empty spot collection, with the natural order based comparator.
	 */
	public SpotCollection() {
		this(new TreeMap<Integer, List<Spot>>());
	}
	
	/*
	 * METHODS
	 */
	
	/**
	 * Return the closest {@link Spot} to the given location (encoded as a 
	 * Spot), contained in the frame <code>frame</code>. If the frame has no spot,
	 * return <code>null</code>.
	 */
	public final Spot getClosestSpot(final Spot location, final int frame) {
		final List<Spot> spots = content.get(frame);
		float d2;
		float minDist = Float.POSITIVE_INFINITY;
		Spot target = null;
		for(Spot s : spots) {
			d2 = s.squareDistanceTo(location);
			if (d2 < minDist) {
				minDist = d2;
				target = s;
			}
		}
		return target;
	}
	
	/**
	 * Return the <code>n</code> closest {@link Spot} to the given location (encoded as a 
	 * Spot), contained in the frame <code>frame</code>. If the number of 
	 * spots in the frame is exhausted, a shorter set is returned.
	 * <p>
	 * The list is ordered by increasing distance to the given location.
	 */
	public final List<Spot> getNClosestSpots(final Spot location, final int frame, int n) {
		final List<Spot> spots = content.get(frame);
		final TreeMap<Float, Spot> distanceToSpot = new TreeMap<Float, Spot>();
		
		float d2;
		for(Spot s : spots) {
			d2 = s.squareDistanceTo(location);
			distanceToSpot.put(d2, s);
		}

		final List<Spot> selectedSpots = new ArrayList<Spot>(n);
		final Iterator<Float> it = distanceToSpot.keySet().iterator();
		while (n > 0 && it.hasNext()) {
			selectedSpots.add(distanceToSpot.get(it.next()));
			n--;
		}
		return selectedSpots;
	}

	/**
	 * Finds the frame this spot belongs to if it is in this collection. If it is not
	 * in this collection, return <code>null</code>.
	 */
	public final Integer getFrame(final Spot spot) {
		Integer frame = null;
		for(Integer key : content.keySet()) {
			if (content.get(key).contains(spot)) {
				frame = key;
				break;
			}
		}
		return frame;
	}
	
	/**
	 * Return the total number of spots in this collection, over all frames.
	 */
	public final int getNSpots() {
		int nspots = 0;
		for(List<Spot> spots : content.values())
			nspots += spots.size();
		return nspots;
	}
	
	/**
	 * Return a new list made of all the spot in this collection.
	 * <p>
	 * Spots are listed according to the comparator given to the content
	 * treemap (if none was given, the it is the natural order for the frame 
	 * they belong to).
	 */
	public final List<Spot> getAllSpots() {
		List<Spot> allSpots = new ArrayList<Spot>(getNSpots()); 
		for(List<Spot> spots : content.values())
			allSpots.addAll(spots);
		return allSpots;
	}
	
	/*
	 * ITERABLE & co
	 */
	
	/**
	 * Return an iterator that iterates over all the spots contained in this collection.
	 */
	@Override
	public Iterator<Spot> iterator() {
		return getAllSpots().iterator();
	}
	
	/**
	 * Return an iterator that iterates over the spots in the given frame.
	 */
	public Iterator<Spot> iterator(Integer frame) {
		return content.get(frame).iterator();
	}

	/*
	 * SORTEDMAP
	 */
	
	@Override
	public void clear() {
		content.clear();
	}
	
	@Override
	public int size() {
		return content.size();
	}

	@Override
	public boolean isEmpty() {
		return content.isEmpty();
	}

	@Override
	public boolean containsKey(Object key) {
		return content.containsKey(key);
	}

	@Override
	public boolean containsValue(Object value) {
		return content.containsValue(value);
	}

	@Override
	public List<Spot> get(Object key) {
		return content.get(key);
	}

	@Override
	public List<Spot> put(Integer key, List<Spot> value) {
		return content.put(key, value);
	}

	@Override
	public List<Spot> remove(Object key) {
		return content.remove(key);
	}

	@Override
	public void putAll(Map<? extends Integer, ? extends List<Spot>> map) {
		content.putAll(map);
	}

	@Override
	public Comparator<? super Integer> comparator() {
		return content.comparator();
	}

	@Override
	public SortedMap<Integer, List<Spot>> subMap(Integer fromKey, Integer toKey) {
		return content.subMap(fromKey, toKey);
	}

	@Override
	public SortedMap<Integer, List<Spot>> headMap(Integer toKey) {
		return content.headMap(toKey);
	}

	@Override
	public SortedMap<Integer, List<Spot>> tailMap(Integer fromKey) {
		return content.tailMap(fromKey);
	}

	@Override
	public Integer firstKey() {
		return content.firstKey();
	}

	@Override
	public Integer lastKey() {
		return content.lastKey();
	}

	@Override
	public Set<Integer> keySet() {
		return content.keySet();
	}

	@Override
	public Collection<List<Spot>> values() {
		return content.values();
	}

	@Override
	public Set<java.util.Map.Entry<Integer, List<Spot>>> entrySet() {
		return content.entrySet();
	}

}
