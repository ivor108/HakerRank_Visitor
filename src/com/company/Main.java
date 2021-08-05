package com.company;

import java.util.*;

import java.util.Scanner;

enum Color {
    RED, GREEN
}

abstract class Tree {

    private int value;
    private Color color;
    private int depth;

    public Tree(int value, Color color, int depth) {
        this.value = value;
        this.color = color;
        this.depth = depth;
    }

    public int getValue() {
        return value;
    }

    public Color getColor() {
        return color;
    }

    public int getDepth() {
        return depth;
    }

    public abstract void accept(TreeVis visitor);
}

class TreeNode extends Tree {

    private ArrayList<Tree> children = new ArrayList<>();

    public TreeNode(int value, Color color, int depth) {
        super(value, color, depth);
    }

    public void accept(TreeVis visitor) {
        visitor.visitNode(this);

        for (Tree child : children) {
            child.accept(visitor);
        }
    }

    public void addChild(Tree child) {
        children.add(child);
    }
}

class TreeLeaf extends Tree {

    public TreeLeaf(int value, Color color, int depth) {
        super(value, color, depth);
    }

    public void accept(TreeVis visitor) {
        visitor.visitLeaf(this);
    }
}

abstract class TreeVis
{
    public abstract int getResult();
    public abstract void visitNode(TreeNode node);
    public abstract void visitLeaf(TreeLeaf leaf);

}

class SumInLeavesVisitor extends TreeVis {
    int result = 0;
    public int getResult() {
        return result;
    }

    public void visitNode(TreeNode node) {

    }

    public void visitLeaf(TreeLeaf leaf) {
        result += leaf.getValue();
    }
}

class ProductOfRedNodesVisitor extends TreeVis {
    long result = 1;
    private final int M = 1000000007;
    public int getResult() {
        return (int) result;
    }

    public void visitNode(TreeNode node) {
        if(node.getColor().equals(Color.RED))
            result = (result * node.getValue()) % M;
    }

    public void visitLeaf(TreeLeaf leaf) {
        if(leaf.getColor().equals(Color.RED))
            result = (result * leaf.getValue()) % M;
    }
}

class FancyVisitor extends TreeVis {

    int sum_depth = 0;
    int sum_green = 0;
    public int getResult() {
        return Math.abs(sum_depth - sum_green);
    }

    public void visitNode(TreeNode node) {
        if(node.getDepth() % 2 == 0)
            sum_depth += node.getValue();
    }

    public void visitLeaf(TreeLeaf leaf) {
        if(leaf.getColor().equals(Color.GREEN))
            sum_green += leaf.getValue();
    }
}



public class Main {

    private static int [] values;
    private static Color [] colors;
    private static HashMap<Integer, HashSet<Integer>> map;

    public static Tree solve() {
        //read the tree from STDIN and return its root as a return value of this function
        Scanner sc = new Scanner(System.in);
        int n = Integer.parseInt(sc.nextLine());

        values = new int[n];
        colors = new Color[n];
        map = new HashMap<>(n);


        for (int i = 0; i < values.length; i++)
            values[i] = sc.nextInt();
        for (int i = 0; i < values.length; i++)
            colors[i] = sc.nextInt() == 0 ? Color.RED : Color.GREEN;

        for (int i = 0; i < n - 1; i++) {
            int u = sc.nextInt();
            int v = sc.nextInt();

            /* Edges are undirected: Add 1st direction */
            HashSet<Integer> uNeighbors = map.get(u);
            if (uNeighbors == null) {
                uNeighbors = new HashSet<>();
                map.put(u, uNeighbors);
            }
            uNeighbors.add(v);

            /* Edges are undirected: Add 2nd direction */
            HashSet<Integer> vNeighbors = map.get(v);

            if (vNeighbors == null) {
                vNeighbors = new HashSet<>();
                map.put(v, vNeighbors);
            }
            vNeighbors.add(u);
        }

        sc.close();

        TreeNode root = new TreeNode(values[0], colors[0], 0);
        if (n == 1) {
            return root;
        }
        Create_tree(root, 1);
        return root;
    }


    public static void Create_tree(TreeNode root, Integer number)
    {
        for (Integer child : map.get(number))
        {
            map.get(child).remove(number);

            HashSet<Integer> grandChildren = map.get(child);
            boolean is_node = (grandChildren != null && !grandChildren.isEmpty());

            Tree current_tree;
            if (is_node) {
                current_tree = new TreeNode(values[child - 1], colors[child - 1], root.getDepth() + 1);
            } else {
                current_tree = new TreeLeaf(values[child - 1], colors[child - 1], root.getDepth() + 1);
            }
            root.addChild(current_tree);

            if(current_tree instanceof TreeNode)
            {
                Create_tree( (TreeNode)current_tree, child);
            }


        }
    }


    public static void main(String[] args) {


        Tree root = solve();
        SumInLeavesVisitor vis1 = new SumInLeavesVisitor();
        ProductOfRedNodesVisitor vis2 = new ProductOfRedNodesVisitor();
        FancyVisitor vis3 = new FancyVisitor();

        root.accept(vis1);
        root.accept(vis2);
        root.accept(vis3);

        int res1 = vis1.getResult();
        int res2 = vis2.getResult();
        int res3 = vis3.getResult();

        System.out.println(res1);
        System.out.println(res2);
        System.out.println(res3);


    }
}
