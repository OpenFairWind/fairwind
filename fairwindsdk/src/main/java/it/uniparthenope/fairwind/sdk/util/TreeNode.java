package it.uniparthenope.fairwind.sdk.util;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by raffaelemontella on 01/03/2017.
 */

public class TreeNode<T> {

    private T data;
    private TreeNode<T> parent;
    private List<TreeNode<T>> children;

    public TreeNode<T> getParent() { return parent; }
    public List<TreeNode<T>> getChildren() { return children; }
    public T getData() { return data; }

    public TreeNode() {

    }

    public TreeNode(T data) {
        this.data=data;
    }

    public void setParent(TreeNode<T> parent) {
        this.parent=parent;
    }
    public TreeNode<T>  addChildren(T data) {
        TreeNode<T> node=new TreeNode<T>(data);
        node.parent=this;
        if (children==null) {
            children=new ArrayList<TreeNode<T>>();
        }
        children.add(node);
        return node;
    }

}
