package CodingProject.budgetvision.controller;

public class LinkedList <E> {

    /**
     * The Linked list is parameterized. The Linked list can be specified to any data type at run-time.
     */
    Node <E> head;

    /**
     * Overloaded LinkedList constructor which declares the current head node of the Linked list.
     * @param data of some data type element E which represents the Node object to be created.
     */
    public LinkedList(E data){
        this.head = new Node(data); //Node object of some data type is the initial head of the linked list.
    }


    /**
     * This method inserts a new Node object with the specified data at the head of the node in ASCENDING ORDER (LOW TO HIGH).
     * Time complexity O(n) to insert node in Ascending order.
     * @param data of some data type element E.
     */
    public void insertAtHeadAscendingSort(E data){

        if(data instanceof String){

            //value of the data given, AKA...COST .
            String dataStr = data.toString();
            double dataValue = Double.parseDouble(dataStr.substring(dataStr.lastIndexOf(' ')).trim());

            Node currentNode = this.head; //currentNode node object aliasing the head node object.
            Node <E> temp = new Node <E> (data);
            Node previousNode = null; //initially previous node is null.

            //finding the node before insertion.
            while(currentNode != null && Double.parseDouble(currentNode.toString().substring(currentNode.toString().lastIndexOf(' ')).trim()) < dataValue){
                previousNode = currentNode; //previousNode aliasing the currentNode which aliases this.head.
                currentNode = currentNode.getNextNode(); //iterating the current node.
            }

            //when iterating the node before insertion is not found because current node data >= data value. The node needs to be added to the front of the linked list.
            if(this.head == currentNode){
                temp.setNextNode(currentNode);
                this.head = temp;
            }
            else{
                //otherwise the node before insertion is found.
                temp.setNextNode(currentNode); //the temp will reference its next node to the current node.
                previousNode.setNextNode(temp); // setting the previous node to the temp. this.head will become previousNode due to previousNode aliasing currentNode which aliases this.head
            }

        }

    }

    /**
     * This method inserts a new Node object with the specified data at the head of the node in DESCENDING ORDER (HIGH TO LOW).
     * Time complexity is O(n) to insert node in descending order.
     * @param data of some data type element E.
     */
    public void insertAtHeadDescendingSort(E data){

        if(data instanceof String){

            //value of the data given, AKA...COST .
            String dataStr = data.toString();
            double dataValue = Double.parseDouble(dataStr.substring(dataStr.lastIndexOf(' ')).trim());

            Node currentNode = this.head; //currentNode node object aliasing the head node object.
            Node <E> temp = new Node <E> (data);
            Node previousNode = null; //initially previous node is null.

            //finding the node before insertion.
            while(currentNode != null && Double.parseDouble(currentNode.toString().substring(currentNode.toString().lastIndexOf(' ')).trim()) > dataValue){
                previousNode = currentNode; //previousNode aliasing the currentNode which aliases this.head.
                currentNode = currentNode.getNextNode(); //iterating the current node.
            }

            //the node before insertion is not found. The node needs to be added to the front of the linked list.
            if(this.head == currentNode){
                temp.setNextNode(currentNode);
                this.head = temp;
            }
            else {
                //otherwise the node before the insertion is found.
                temp.setNextNode(currentNode); //the temp will reference its next node to the current node.
                previousNode.setNextNode(temp); // setting the previous node to the temp. this.head will become previousNode due to previousNode aliasing currentNode which aliases this.head
            }
        }

    }

    /**
     * Inserting new head into the linked list in alphabetical descending order.
     * Time complexity O(n) to insert node in alphabetical descending order.
     */
    public void insertAtHeadAlphaDescendingSort(E data){

        if(data instanceof String){

            //value of the data given. Converted to all lowercase to compare chars regardless of uppercase or lowercase.
            String dataStr = data.toString().toLowerCase();

            Node currentNode = this.head; //currentNode node object aliasing the head node object.
            Node <E> temp = new Node <E> (data);
            Node previousNode = null; //initially previous node is null.

            //finding the node before insertion.
            while(currentNode != null && currentNode.toString().toLowerCase().compareTo(dataStr) > 0){
                previousNode = currentNode; //previousNode aliasing the currentNode which aliases this.head.
                currentNode = currentNode.getNextNode(); //iterating the current node.
            }

            //the node before insertion is not found. The node needs to be added to the front of the linked list.
            if(this.head == currentNode){
                temp.setNextNode(currentNode);
                this.head = temp;
            }
            else {
                temp.setNextNode(currentNode); //the temp will reference its next node to the current node.
                previousNode.setNextNode(temp); // setting the previous node to the temp. this.head will become previousNode due to previousNode aliasing currentNode which aliases this.head
            }
        }

    }

    /**
     * Inserting new head into the linked list in alphabetical ascending order.
     * Time complexity O(n) to insert node in alphabetical ascending order.
     */
    public void insertAtHeadAlphaAscendingSort(E data){

        if(data instanceof String){

            //value of the data given. Converted to all lowercase to compare chars regardless of uppercase or lowercase.
            String dataStr = data.toString().toLowerCase();

            Node currentNode = this.head; //currentNode node object aliasing the head node object.
            Node <E> temp = new Node <E> (data);
            Node previousNode = null; //initially previous node is null.

            //finding the node before insertion.
            while(currentNode != null && currentNode.toString().toLowerCase().compareTo(dataStr) < 0){
                previousNode = currentNode; //previousNode aliasing the currentNode which aliases this.head.
                currentNode = currentNode.getNextNode(); //iterating the current node.
            }

            if(head == currentNode){
                temp.setNextNode(currentNode);
                head = temp;
            }
            else {
                temp.setNextNode(currentNode); //the temp will reference its next node to the current node.
                previousNode.setNextNode(temp); // setting the previous node to the temp. this.head will become previousNode due to previousNode aliasing currentNode which aliases this.head
            }
        }

    }

    /**
     * This method returns the head of the linked list.
     * @return this.head which is the head of the linked list.
     */
    public Node<E> getHead(){
        return this.head;
    }

    /**
     * This method prints each cost node of the linked list. O(n) time complexity.
     */
    public String printLinkedList(){
        StringBuilder strBuilder = new StringBuilder();
        String str = "";

        Node currentNode = head;

        while(currentNode != null){
            System.out.println(currentNode.toString());
            strBuilder.append(currentNode.toString()).append("\n");
            currentNode = currentNode.getNextNode();
        }

        str = strBuilder.toString();

        return str;

    }
}
