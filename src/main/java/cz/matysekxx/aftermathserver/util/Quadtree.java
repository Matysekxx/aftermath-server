package cz.matysekxx.aftermathserver.util;

import java.util.ArrayList;
import java.util.List;

/// A Quadtree implementation for efficient spatial querying.
///
/// Used to partition a 2D space into smaller quadrants to quickly find objects
/// within a specific area.
public class Quadtree<T extends Spatial> {

    private static final int MAX_OBJECTS = 10;
    private static final int MAX_LEVELS = 5;

    private final int level;
    private final List<T> objects;
    private final Rectangle bounds;
    @SuppressWarnings("rawtypes")
    private Quadtree[] nodes;

    public Quadtree(int level, Rectangle bounds) {
        this.level = level;
        this.bounds = bounds;
        this.objects = new ArrayList<>();
    }

    /// Clears the quadtree.
    public void clear() {
        objects.clear();
        if (nodes != null) {
            for (@SuppressWarnings("rawtypes") Quadtree node : nodes) {
                node.clear();
            }
            nodes = null;
        }
    }

    /// Splits the node into 4 subnodes.
    private void split() {
        final int subWidth = this.bounds.width() / 2;
        final int subHeight = this.bounds.height() / 2;
        final int x = this.bounds.x();
        final int y = this.bounds.y();

        nodes = new Quadtree[4];
        nodes[0] = new Quadtree<>(level + 1, Rectangle.of(x + subWidth, y, subWidth, subHeight));
        nodes[1] = new Quadtree<>(level + 1, Rectangle.of(x, y, subWidth, subHeight));
        nodes[2] = new Quadtree<>(level + 1, Rectangle.of(x, y + subHeight, subWidth, subHeight));
        nodes[3] = new Quadtree<>(level + 1, Rectangle.of(x + subWidth, y + subHeight, subWidth, subHeight));
    }

    /// Determine which node the object belongs to. -1 means object cannot completely fit within a child node.
    private int getIndex(T p) {
        int index = -1;
        final double verticalMidpoint = bounds.x() + (bounds.width() >> 1);
        final double horizontalMidpoint = bounds.y() + (bounds.height() >> 1);

        final boolean topQuadrant = (p.getY() < horizontalMidpoint);
        final boolean bottomQuadrant = (p.getY() >= horizontalMidpoint);

        if (p.getX() < verticalMidpoint) {
            if (topQuadrant) index = 1;
            else if (bottomQuadrant) index = 2;
        } else if (p.getX() >= verticalMidpoint) {
            if (topQuadrant) index = 0;
            else if (bottomQuadrant) index = 3;
        }
        return index;
    }

    /// Insert the object into the quadtree.
    @SuppressWarnings("unchecked")
    public void insert(T p) {
        if (nodes != null) {
            final int index = getIndex(p);
            if (index != -1) {
                nodes[index].insert(p);
                return;
            }
        }

        objects.add(p);

        if (objects.size() > MAX_OBJECTS && level < MAX_LEVELS) {
            if (nodes == null) split();
            int i = 0;
            while (i < objects.size()) {
                int index = getIndex(objects.get(i));
                if (index != -1) {
                    nodes[index].insert(objects.remove(i));
                } else {
                    i++;
                }
            }
        }
    }

    /// Return all objects that could collide with the given object.
    @SuppressWarnings("unchecked")
    public List<T> retrieve(List<T> returnObjects, T p) {
        final int index = getIndex(p);
        if (index != -1 && nodes != null) {
            nodes[index].retrieve(returnObjects, p);
        }
        returnObjects.addAll(objects);
        return returnObjects;
    }
}