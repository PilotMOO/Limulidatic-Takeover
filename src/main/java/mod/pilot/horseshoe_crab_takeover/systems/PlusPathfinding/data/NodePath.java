package mod.pilot.horseshoe_crab_takeover.systems.PlusPathfinding.data;

import org.jetbrains.annotations.NotNull;

import java.util.Iterator;

public class NodePath<N extends INode> implements Iterable<N>{
    protected NodePath(N[] nodes, boolean flip){
        this.nodes = nodes;
        this.flip = flip;
    }
    private final N[] nodes;
    private boolean flip;
    public void flip(){ flip = !flip; }
    public void startFirst(){ flip = true; }


    @Override public @NotNull Iterator<N> iterator() { return new NodePathIterator(); }
    private class NodePathIterator implements Iterator<N>{
        protected NodePathIterator(){
            array = nodes;
            size = array.length;
            index = flip ? size : 0;
        }
        private final int size;
        private final N[] array;
        private int index;

        @Override
        public boolean hasNext() {
            return index > -1 && index < size;
        }
        @Override public N next() { return array[index += flip ? -1 : 1]; }
    }

    public static class Builder<N extends INode>{
        private INode[] nodes;
        private int size;
        private int indexFull;
        private Builder(int expectedSize){
            nodes = new INode[expectedSize];
            size = expectedSize;
            indexFull = 0;
        }

        public void unpackToPath(N endNode, int gridSize){
            final int div4 = Math.min(gridSize, 16) / 4;
            INode current = endNode;
            do{ add(current, div4); }
            while ((current = current.getParent()) != null);
        }

        private void add(INode iNode, int grow){
            if (size <= indexFull){
                int newSize = size + grow;
                INode[] array = new INode[newSize];
                System.arraycopy(nodes, 0, array, 0, indexFull);
                nodes = array;
                size = newSize;
            }
            nodes[++indexFull] = iNode;
        }

        @SuppressWarnings("unchecked")
        public NodePath<N> createPath(boolean startFirst){
            return new NodePath<>((N[])nodes, startFirst);
        }
    }
}
