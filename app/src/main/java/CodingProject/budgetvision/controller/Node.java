package CodingProject.budgetvision.controller;

public class Node <E> {
    /**
     * Node class is parameterized with E.
     */

    E data; //the data stored in this Node.

    Node <E> nextNode; //the next node in comparison to the current Node object.

    /**
     * Overloaded Node constructor which takes some data type of data and sets it.
     * @param data
     */
    public Node(E data){
        this.data = data;
    }

    public E getData(){
        return data;
    }

    public void setData(E data){
        this.data = data;
    }

    public void setNextNode(Node <E>nextNode){
        this.nextNode = nextNode;
    }

    public Node <E> getNextNode(){
        return this.nextNode;
    }

    /**
     * Overrided toString method which prints out the current data for this Node object.
     * @return string representation of the data.
     */
    @Override
    public String toString(){
        return "" + data;
    }
}
