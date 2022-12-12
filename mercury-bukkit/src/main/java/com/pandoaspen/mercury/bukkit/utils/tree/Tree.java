package com.pandoaspen.mercury.bukkit.utils.tree;

import lombok.Data;
import lombok.SneakyThrows;

import java.io.PrintStream;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class Tree {


    private final Supplier<Map<String, TreeNode>> mapConstructor;
    private final Map<String, TreeNode> root;

    public Tree() {
        this(HashMap::new);
    }

    @SneakyThrows
    public Tree(final Supplier<Map<String, TreeNode>> mapConstructor) {
        this.mapConstructor = mapConstructor;
        this.root = mapConstructor.get();
    }

    public Tree branch(String key) {
        if (this.root.containsKey(key)) {
            TreeNode treeNode = this.root.get(key);
            if (treeNode.isTree()) {
                return (Tree) treeNode.getValue();
            }
            throw new RuntimeException("Cannot branch from key: " + key + ". This key is occupied by a value node");
        }
        Tree tree = new Tree(this.mapConstructor);
        this.root.put(key, new TreeNode(key, tree, true));
        return tree;
    }

    public void put(String key, Object value) {
        this.root.put(key, new TreeNode(key, value, false));
    }

    public boolean containsKey(String key) {
        return root.containsKey(key);
    }

    public String getString(String... path) throws TreeWalkException {
        return get(path).toString();
    }

    public Tree getTree(String... path) throws TreeWalkException {
        return get(path);
    }

    public Object remove(String... path) throws TreeWalkException {
        String[] parentPath = Arrays.copyOf(path, path.length - 1);
        String finalPath = path[path.length - 1];
        Tree tree = (Tree) follow(parentPath).getValue();
        return tree.root.remove(finalPath).getValue();
    }

    private TreeNode follow(String... path) throws TreeWalkException {
        if (path == null || path.length == 0) {
            throw new TreeWalkException("Invalid path: " + Arrays.toString(path));
        }

        Map<String, TreeNode> root = this.root;
        String key;
        TreeNode treeNode = null;

        // Walk down all but last path key
        for (int i = 0; i < path.length; i++) {
            treeNode = root.get(path[i]);
            if (treeNode == null || !treeNode.isTree()) {
                // Throw Exception? Treenode null or not represents tree
                throw new TreeWalkException("Cannot follow key: " + path[i]);
            }
            root = ((Tree) treeNode.getValue()).root;
        }

        return treeNode;
    }


    public <T> T get(String... path) throws TreeWalkException {
        String[] parentPath = Arrays.copyOf(path, path.length - 1);
        String finalPath = path[path.length - 1];

        Tree tree = (Tree) follow(parentPath).getValue();
        if (!tree.containsKey(finalPath)) {
            // Throw Exception? Treenode value null
            return null;
        }

        return (T) tree.root.get(finalPath).getValue();
    }

    public void printTree() {
        walk(0, this, System.out);
    }


    private void walk(int depth, Tree tree, PrintStream out) {
        String prefix = IntStream.range(0, depth).mapToObj(i -> "  ").collect(Collectors.joining());
        for (TreeNode treeNode : tree.root.values()) {
            if (treeNode.isTree()) {
                out.printf("%s%s:\n", prefix, treeNode.getKey());
                walk(depth + 1, (Tree) treeNode.getValue(), out);
            } else {
                Object value = treeNode.getValue();
                out.printf("%s%s: %s <%s>\n", prefix, treeNode.getKey(), value.toString(), value.getClass().getName());
            }

        }
    }


}

@Data
class TreeNode {
    private final String key;
    private final Object value;
    private final boolean isTree;
}
/*
    private TreeNode follow(String... path) {
        if (path == null || path.length == 0) {
            // Throw  exception? Invalid path
            return null;
        }

        Map<String, TreeNode> root = this.root;
        String key;
        TreeNode treeNode;

        // Walk down all but last path key
        for (int i = 0; i < path.length - 1; i++) {
            key = path[i];
            treeNode = root.get(key);
            if (treeNode == null || !treeNode.isTree()) {
                // Throw Exception? Treenode null or not represents tree
                return null;
            }
            root = ((Tree) treeNode.getValue()).root;
        }

        key = path[path.length - 1];
        treeNode = root.get(key);

        if (treeNode == null) {
            // Throw Exception? Final Treenode null
            return null;
        }

        return treeNode;
    }
 */