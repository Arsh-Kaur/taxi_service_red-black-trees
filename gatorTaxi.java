import java.io.*;
import java.util.*;

public class gatorTaxi {

    public static void main(String[] args) throws IOException{

        // if file name not mentioned in the command
        if(args.length == 0) {
           System.out.println("Usage: java gatorTaxi input_file_name");
           System.exit(1);
        }

        try{
            GatorTaxiRide gatorTaxiRide = new GatorTaxiRide();

            //BufferedReader to read the commands from the input file
            FileReader inputFile = new FileReader(args[0]);
            BufferedReader bufferedReader = new BufferedReader(inputFile);
            
            //BufferedWriter to write the output to the output file - output_file.txt
            FileWriter outputFile = new FileWriter("output_file.txt");
            BufferedWriter bufferedWriter = new BufferedWriter(outputFile);

            //Set the PrintStream to output all system output statements to output_file
            PrintStream printStream = new PrintStream(new FileOutputStream("output_file.txt"));
            System.setOut(printStream);

            String line;
            while ((line = bufferedReader.readLine()) != null) {
                String[] tokens = line.split("\\(");
                //Command Name
                String functionName = tokens[0];
                //Array to store the arguments of the command
                String[] arguments = new String[0];

                if (tokens.length > 1) {
                    String argsString = tokens[1].replace(")", "");
                    arguments = argsString.split(",");
                }

                // Call the corresponding gatorTaxiRide function based on the function name
                switch(functionName){
                    // Call function Print(rideNumber) or Print(rideNumber1, rideNumber2) // operation 1,2 from requirement 
                    case "Print":
                        int rideNumber1 = Integer.parseInt(arguments[0]);
                        if (arguments.length == 1) {
                            gatorTaxiRide.Print(rideNumber1);
                        } else if(arguments.length == 2){
                            int rideNumber2 = Integer.parseInt(arguments[1]);
                            gatorTaxiRide.Print(rideNumber1, rideNumber2);
                        } else{
                            System.out.println("Invalid function arguments: " + functionName);
                        }
                        break;
                    // Call function Insert (rideNumber, rideCost, tripDuration) // operation 3 from requirement 
                    case "Insert":
                        if (arguments.length == 3) {
                            int rideNumber = Integer.parseInt(arguments[0]);
                            int rideCost = Integer.parseInt(arguments[1]);
                            int tripDuration = Integer.parseInt(arguments[2]);
                            gatorTaxiRide.Insert(rideNumber, rideCost, tripDuration);
                        } else{
                            System.out.println("Invalid function arguments: " + functionName);
                        }
                        break;
                    // Call function GetNextRide() // operation 3 from requirement 
                    case "GetNextRide":
                        gatorTaxiRide.GetNextRide();
                        break;
                    // Call CancelRide(rideNumber) // operation 4 from requirement
                    case "CancelRide":
                        if(arguments.length == 1){
                            int rideNumberCancel = Integer.parseInt(arguments[0]);
                            gatorTaxiRide.CancelRide(rideNumberCancel);
                        }else{
                            System.out.println("Invalid function arguments: " + functionName);
                        }
                        break;
                    // Call UpdateTrip(rideNumber, new_tripDuration) // operation 5 from requirement 
                    case "UpdateTrip":
                        if(arguments.length == 2){
                            int rideNumberUpdate = Integer.parseInt(arguments[0]);
                            int newTripDuration = Integer.parseInt(arguments[1]);
                            gatorTaxiRide.UpdateTrip(rideNumberUpdate, newTripDuration);
                        }else{
                            System.out.println("Invalid function arguments: " + functionName);
                        }
                        break;
                    default:
                        System.out.println("Invalid function: " + functionName);
                        break;
                }
            }
            // bufferedWriter.flush();
            bufferedReader.close();
            bufferedWriter.close();
        }catch (FileNotFoundException e) {
            //If input file not exists
            System.out.println("Input file not found: "+ e.getMessage());
        }catch (IOException e) {
            //IO Exception from bufferedReader or BufferedWriter
            System.out.println("IOException ocurred: "+ e.getMessage());
        }catch (Exception e) {
            //Any other Exception
            System.out.println("Exception ocurred: "+ e.getMessage());
        }
    }
}

class Ride {

    // Ride is identified by triplet - (rideNumber, rideCost, tripDuration)
    // @ridenumber - unique integer identifier for each ride
    // @rideCodet - estimated cost (in integer dollars) for the ride
    // @tripDuration - total time (in integer minutes) needed to get from pickup to destination

    int rideNumber;
    int rideCost;
    int tripDuration;    
    Ride(int rideNumber, int rideCost, int tripDuration) {
        this.rideNumber = rideNumber;
        this.rideCost = rideCost;
        this.tripDuration = tripDuration;
    }
}

class Node {

    // Node corresponds to a RedBlackTree node - basic node structure : data,left,right,parent,color
    // @rideNumber - RBT is ordered by rideNumber
    // @ride - RBT stores (rideNumber, rideCost, tripDuration) triplets in this variable
    // @leftChild - points to left child of the node
    // @rightChild - points to right child of the node
    // @parent - points to parent of the node
    // @ color - refers to the node color which can be either RED or BLACK (identified as enum class)

    int rideNumber;
    Node leftChild;
    Node rightChild;
    Node parent;
    NodeColor color;
    Ride ride;

    public Node(int rideNumber, Ride ride) {
        this.rideNumber = rideNumber;
        this.ride = ride;
        this.leftChild = null;
        this.rightChild = null;
        this.parent = null;
        //any new node is inserted as a RED node
        this.color = NodeColor.RED;
    }
}

enum NodeColor {
    //NodeColor corresponds to the node color 
    RED,
    BLACK
}

class MinHeap {

    // MinHeap - heapArray is used to store ride triplets ordered by rideCost, tripDuration in a ArrayList 
    private ArrayList<Ride> heapArray;
    // rideIndexMap is used to map the rideNumber to its index in the heapArray 
    private Map<Integer, Integer> rideIndexMap;
    private int maxSize = 2000;

    //MinHeap constructor to instantiate the class variables
    public MinHeap() {
        heapArray = new ArrayList<Ride>();
        rideIndexMap = new HashMap<>();
    }

    //If the heapsize is 0, means the heap is empty.
    public boolean isEmpty() {
        return heapArray.size() == 0;
    }

    //Insert a new Ride to heap
    public void insert(Ride ride) {
        if (heapArray.size() == maxSize) {
            return ;
        }

        heapArray.add(ride);
        rideIndexMap.put(ride.rideNumber, heapArray.size()-1);
        //heapify up from the given index so it maintains heap property
        heapify_up(heapArray.size()-1);
    }

    //Get the Ride with minimum cost/tripDuration, which will always be stored at 0th index
    public Ride removeMin() {
        if(heapArray.size() == 0){
            return null;
        }    
        return heapArray.get(0);
    }

    //Delete a ride from heap given the rideNumber
    public void delete(int rideNumber) {
        if (rideIndexMap == null) {
            return;
        }
        if(rideIndexMap.containsKey(rideNumber)){
            // get index to be deleted
            int rindex = rideIndexMap.get(rideNumber);
            //get last index of the heap
            int lastIndex = heapArray.size() - 1;

            if(rindex != lastIndex){
                //move value at last index to indexValue to be deleted and then remove value at lastIndex
                rideIndexMap.put(heapArray.get(lastIndex).rideNumber, rindex);
                heapArray.set(rindex,heapArray.get(lastIndex));
                heapArray.remove(lastIndex);
                //heapify down from the given index so it maintains heap property
                heapify_down(rindex);
            }else{
                //move value from lastIndex
                heapArray.remove(lastIndex);
            }
            
        }
        
    }

    //called from insert
    private void heapify_up(int index) {
        int parentIndex = (index - 1) / 2;
        while (index > 0 && (heapArray.get(parentIndex).rideCost > heapArray.get(index).rideCost || (heapArray.get(index).rideCost == heapArray.get(parentIndex).rideCost &&  heapArray.get(parentIndex).tripDuration > heapArray.get(index).tripDuration))) {
            // update values in map
            rideIndexMap.put(heapArray.get(index).rideNumber, parentIndex);
            rideIndexMap.put(heapArray.get(parentIndex).rideNumber, index);

            //swap values in heap at index and parentIndex if heap condition not satisfied
            Ride temp = heapArray.get(index);
            heapArray.set(index,heapArray.get(parentIndex));
            heapArray.set(parentIndex,temp);

            // update pointers for index and parentIndex
            index = parentIndex;
            parentIndex = (index - 1) / 2;
        }
    }

    // called from delete
    private void heapify_down(int index) {
        int smallerChildIndex = index;
        int leftChildIndex = 2 * index + 1;
        int rightChildIndex = leftChildIndex + 1;

        if (leftChildIndex < heapArray.size() && (heapArray.get(leftChildIndex).rideCost < heapArray.get(smallerChildIndex).rideCost || (heapArray.get(leftChildIndex).rideCost == heapArray.get(smallerChildIndex).rideCost &&  heapArray.get(leftChildIndex).tripDuration < heapArray.get(smallerChildIndex).tripDuration))) {
            smallerChildIndex = leftChildIndex;
        }
        if (rightChildIndex < heapArray.size() && (heapArray.get(rightChildIndex).rideCost < heapArray.get(smallerChildIndex).rideCost || (heapArray.get(rightChildIndex).rideCost == heapArray.get(smallerChildIndex).rideCost &&  heapArray.get(rightChildIndex).tripDuration < heapArray.get(smallerChildIndex).tripDuration))) {
            smallerChildIndex = rightChildIndex;
        }
        if (smallerChildIndex != index) {
            // update values in map
            rideIndexMap.put(heapArray.get(index).rideNumber, smallerChildIndex);
            rideIndexMap.put(heapArray.get(smallerChildIndex).rideNumber, index);

            //swap values in heap at index and smallerChildIndex if heap condition not satisfied
            Ride temp = heapArray.get(index);
            heapArray.set(index,heapArray.get(smallerChildIndex));
            heapArray.set(smallerChildIndex,temp);

            heapify_down(smallerChildIndex);
        }
    }

}

class RedBlackTree {
    //points to root of RBT
    Node root;

    public RedBlackTree() {
        this.root = null;
    }

    //check if the given node is BLACK in color
    private boolean isBlack(Node node) {
        return node == null || node.color == NodeColor.BLACK;
    }

    //check if RBT is empty
    public boolean isEmpty() {
        return root == null;
    }

    // - search block start - //
    // --------- search node in red black tree -----------//
    public Node search(int rideNumber) {
        Node current = root;  
        while (current != null) {
            if (rideNumber == current.ride.rideNumber) {
                return current;
            }else if (rideNumber < current.ride.rideNumber ) {
                // smaller values will be on the left child
                current = current.leftChild;  
            } else {
                // greater values will be on the right child
                current = current.rightChild;  
            }
        }
        return null;
    }

    // using map
    // ArrayList<Ride> searchRange(int rideNumber1, int rideNumber2, Map<Integer, Ride> ridemap) {
    //     Node node = root;
    //     ArrayList<Ride> allRides = new ArrayList<Ride>();
    //     if (node == null) {
    //         return allRides;
    //     }
        
    //     for(Map.Entry<Integer, Ride> entry : ridemap.entrySet()) {
    //         int rideNumber = entry.getKey();
    //         Ride ride = entry.getValue();
    //         if (rideNumber1 <= ride.rideNumber && ride.rideNumber <= rideNumber2) {
    //             allRides.add(ride);
    //         }
    //     }
    //     return allRides;
    // }

    //  --------- search nodes in range in red black tree -----------//
    ArrayList<Ride> searchRange( int rideNumber1, int rideNumber2) {
        Node node = root;
        ArrayList<Ride> allRides = new ArrayList<Ride>();
        allRides = searchRange(node,rideNumber1, rideNumber2, allRides); 
        return allRides;
    }

    // this functon is basically an inorder traversal to find the nodes in between two values
    ArrayList<Ride> searchRange(Node node, int rideNumber1, int rideNumber2, ArrayList<Ride> allRides) {
        if (node == null) {
            return allRides;
        }
        if (node.rideNumber > rideNumber2) {
            searchRange(node.leftChild, rideNumber1, rideNumber2,allRides);
        } else if (node.rideNumber < rideNumber1) {
            searchRange(node.rightChild, rideNumber1, rideNumber2,allRides);
        } else {            
            searchRange(node.leftChild, rideNumber1, rideNumber2,allRides);
            //add rides found in range
            allRides.add(node.ride);
            searchRange(node.rightChild, rideNumber1, rideNumber2,allRides);
        }
        
        return allRides;
    }
    // - search block finish - //

    // - insertion block start - //
    public void insert(Node newNode) {
        if (root == null) {
            //if this node is root, we will insert this as BLACK and don't need to fix any voilation
            root = newNode;
            root.color = NodeColor.BLACK;
            return;
        } else {
            Node parent = null;
            Node current = root;
            // find the index of parent where node should be inserted
            while (current != null) {
                parent = current;
                if (newNode.ride.rideNumber < current.ride.rideNumber) {
                    current = current.leftChild;
                } else {
                    current = current.rightChild;
                }
            }

            //set parent of new node
            newNode.parent = parent;
            //check if the new node will be rightchild or leftchild.
            if (newNode.ride.rideNumber < parent.ride.rideNumber) {
                parent.leftChild = newNode;
            } else {
                parent.rightChild = newNode;
            }
            newNode.color = NodeColor.RED;

            //check and fix any red-red voilations that occur on insertion
            fixInsertion(newNode);
        }
    }

    //fix red-red voilation upon deetion
    private void fixInsertion(Node node) {
        Node parent = node.parent;

        //Case : Node is root - Make it black
        if(parent == null){
            node.color = NodeColor.BLACK;
            return;
        } 

        //Case : If parent is black, we have no red-red voilation
        if(parent.color == NodeColor.BLACK ) return;

        Node grandparent = parent.parent;
        Node uncle = null ;

        //Case : If grandparent is null(parent=root) - we will have no voilation as root is always BLACK 
        if (grandparent == null) {
            node.color = NodeColor.RED;
            return;
        }else{
        // next we will check color of uncle and check further
            if (parent == grandparent.leftChild) {
                uncle = grandparent.rightChild;
            }
            else {
                uncle = grandparent.leftChild;
        	}
        }

        if(uncle != null && uncle.color == NodeColor.RED){
            //Case :  if parent and uncle are both red, set both as black and grandparent as black
            parent.color = NodeColor.BLACK;
            uncle.color = NodeColor.BLACK;
            grandparent.color = NodeColor.RED;

            //fix any voilation that may have ocuured after changing grandparent color to red
            fixInsertion(grandparent);
        } else{
            //left rotate condition -  if newNode is the right child of parent who is left child
            if (parent == grandparent.leftChild && node == parent.rightChild) {
                node=rotateLeft(parent);
                parent = node.parent;
            }
            //right rotate condition -  if newNode is the left child of parent who is right child
            else if (parent == grandparent.rightChild && node == parent.leftChild) {
                node= rotateRight(parent);
                parent = node.parent;
            }

            parent.color = NodeColor.BLACK;
            grandparent.color = NodeColor.RED;
            if (node == parent.leftChild) {
                rotateRight(grandparent);
            }
            else {
                rotateLeft(grandparent);
            }
        }
    }
    // - insertion bloack finish - //

    // - deletion block start -//
    void deleteNode(int rideNumber){
        // given the rideNumber, use the already given function to search the ride
        Node node = search(rideNumber);
        if(node== null) return;

        Node child, parent;
        NodeColor color;

        // Case 1 : If node has 2 children
        if ( (node.leftChild!=null) && (node.rightChild!=null) ) {
            Node replace = node;
            replace = replace.rightChild;
            while (replace.leftChild != null)
                // find the inorder successor which will replace the node we will delete
                replace = replace.leftChild; 

            //replace the node with it's inorder successor
            if (node.parent!=null) {
                if (node.parent.leftChild == node)
                    node.parent.leftChild = replace;
                else
                    node.parent.rightChild = replace;
            } else {
                this.root = replace;
            }

            // update the left child, right child and parent pointers for the inorder sucessor node replaced
            child = replace.rightChild;
            parent =replace.parent;
            color = replace.color;

            if (parent == node) {
                parent = replace;
            } else {
                if (child!=null)
                    child.parent = parent;
                parent.leftChild = child;

                replace.rightChild = node.rightChild;
                node.rightChild.parent =  replace;
            }

            // update the pointers of the successor node
            replace.parent = node.parent;
            replace.color = node.color;
            replace.leftChild = node.leftChild;
            node.leftChild.parent = replace;

            // we need to fix deletion if the deleted node was black 
            //because equality the number of black nodes in each path will be voilated
            if (color == NodeColor.BLACK)
                fixDeletion(child, parent);

            node = null;
            return ;
        }

        // case 2 : If node has 0/1 children - set that node's child to the parent's child
        if (node.leftChild !=null) {
            child = node.leftChild;
        } else {
            child = node.rightChild;
        }

        parent = node.parent;
        color = node.color;

        // set the parent of the child of node to be deleted as parent of child
        // replacing the node to be deleted with it's only child
        if (child!=null)
            child.parent = parent;

        // update the left or right child pointers of the parent to node's only child 
        if (parent!=null) {
            if (parent.leftChild == node)
                parent.leftChild = child;
            else
                parent.rightChild = child;
        } else {
            this.root = child;
        }

        // we need to fix deletion if the deleted node was black 
        //because equality the number of black nodes in each path will be voilated
        if (color == NodeColor.BLACK)
            fixDeletion(child, parent);
        node = null;
    }

    //fix black height voilation upon deetion
    private void fixDeletion(Node node, Node parent) {
        Node other;

        //fix voilation until the node deleted is black or we reach the root
        while ((node==null || isBlack(node)) && (node != this.root)) {

            //Case 1 : node is the left child of its parent
            if (parent.leftChild == node) {

                //Get the sibling
                other = parent.rightChild;

                // Case 1a : If the sibling of node is RED
                if (!isBlack(other)) {
                    // Set the color of sibling and parent of node as BLACK.
                    other.color = NodeColor.BLACK;
                    parent.color = NodeColor.RED;
                    //Left-Rotate the parent of node
                    rotateLeft(parent);
                    //Assign the rightChild of the parent of node to sibling.
                    other = parent.rightChild;
                }

                // Case 1b : If the color of both the right and the leftChild of sibling is BLACK
                if ((other.leftChild==null || isBlack(other.leftChild)) &&
                    (other.rightChild==null || isBlack(other.rightChild))) { 
                    //Set the color of sibling as RED
                    other.color = NodeColor.RED;
                    //Assign the parent of node to node
                    node = parent;
                    parent = node.parent;
                } else {
                    // Case 1c: if the color of the rightChild of sibling is BLACK
                    if (other.rightChild==null || isBlack(other.rightChild)) {
                        //Set the color of the leftChild of sibling as BLACK
                        other.leftChild.color = NodeColor.BLACK;
                        //Set the color of sibling as RED
                        other.color = NodeColor.RED;
                        //Right-Rotate sibling
                        rotateRight(other);
                        //Assign the rightChild of the parent of node as sibling.
                        other = parent.rightChild;
                    }

                    //Set the color of sibling as the color of the parent of node
                    other.color =  parent.color;
                    //Set the color of the parent of node as BLACK
                    parent.color =NodeColor.BLACK;
                    //Set the color of the right child of sibling as BLACK
                    other.rightChild.color = NodeColor.BLACK;
                    //Left-Rotate the parent of node
                    rotateLeft(parent);
                    //Set node as the root of the tree
                    node = this.root;
                    break;
                }
            } 

            //Case 2 : node is the left child of its parent
            else {
                //Get the sibling
                other = parent.leftChild;

                // Case 2a : If the sibling of node is RED
                if (!isBlack(other)) {
                    // Set the color of sibling and parent of node as BLACK.
                    other.color = NodeColor.BLACK;
                    parent.color = NodeColor.BLACK;
                    //Right-Rotate the parent of node
                    rotateRight(parent);
                    //Assign the leftChild of the parent of node to sibling.
                    other = parent.leftChild;
                }

                // Case 2b : If the color of both the right and the left Child of sibling is BLACK
                if ((other.leftChild==null || isBlack(other.leftChild)) &&
                    (other.rightChild==null || isBlack(other.rightChild))) {
                    //Set the color of sibling as RED
                    other.color = NodeColor.RED;
                    //Assign the parent of node to node
                    node = parent;
                    parent = node.parent;
                } else {
                    // Case 2c: if the color of the leftChild of sibling is BLACK
                    if (other.leftChild==null || isBlack(other.leftChild)) {
                        //Set the color of the rightChild of sibling as BLACK
                        other.rightChild.color = NodeColor.BLACK;
                        //Set the color of sibling as RED
                        other.color = NodeColor.BLACK;
                        //Left-Rotate sibling
                        rotateLeft(other);
                        //Assign the leftChild of the parent of node as sibling.
                        other = parent.leftChild;
                    }

                    //Set the color of sibling as the color of the parent of node
                    other.color = parent.color;
                    //Set the color of the parent of node as BLACK
                    parent.color = NodeColor.BLACK;
                    //Set the color of the left child of sibling as BLACK
                    other.leftChild.color = NodeColor.BLACK;
                    //Right-Rotate the parent of node
                    rotateRight(parent);
                    //Set node as the root of the tree
                    node = this.root;
                    break;
                }
            }
        }

        //Set the color of node as BLACK.
        if (node!=null)
            node.color = NodeColor.BLACK;
    }
    // - deletion block ends -//

    // - rotation block start -//

    // Left-rotate 
    // nodes on the right is transformed into the arrangements on the left node
    private Node rotateLeft(Node node) {
        Node temp = node.rightChild;
        node.rightChild = temp.leftChild;
        if (temp.leftChild != null) {
            temp.leftChild.parent = node;
        }
        temp.parent = node.parent;
        if (node.parent == null) {
            this.root = temp;
        } else if (node == node.parent.leftChild) {
            node.parent.leftChild = temp;
        } else {
            node.parent.rightChild = temp;
        }
        temp.leftChild = node;
        node.parent = temp;
        return node;
    }

    // Right-rotate 
    // nodes on the left is transformed into the arrangements on the right node
    private Node rotateRight(Node node) {
        Node leftChild = node.leftChild;
        node.leftChild = leftChild.rightChild;

        if (leftChild.rightChild != null) {
            leftChild.rightChild.parent = node;
        }

        leftChild.parent = node.parent;

        if (node.parent == null) {
            root = leftChild;
        } else if (node == node.parent.leftChild) {
            node.parent.leftChild = leftChild;
        } else {
            node.parent.rightChild = leftChild;
        }

        leftChild.rightChild = node;
        node.parent = leftChild;
        return node;
    }
    // - rotations block end - //
}

class GatorTaxiRide {

    //Create an instance of minheap, redblack tree and map of rideNumber to ride
    MinHeap minheap = new MinHeap();
    RedBlackTree redblacktree = new RedBlackTree();
    Map<Integer, Ride> ridemap = new HashMap<Integer,Ride>();

    public GatorTaxiRide() {}

    void Print(int rideNumber) { // log(n)
        //If ride not present, print (0,0,0) 
        if (!ridemap.containsKey(rideNumber)) {
            System.out.println("(0,0,0)");
            return;
        }

        //Iterate the red-black tree to find the ride
        Node node = redblacktree.search(rideNumber);
        if (node != null) {
            Ride ride = node.ride;
            System.out.println("(" + ride.rideNumber + "," + ride.rideCost + ","+ ride.tripDuration + ")");
        }
    }

    void Print(int rideNumber1, int rideNumber2) { //log(n)+S
        //Iterate the tree in inordertraversal and print the rides returned
        ArrayList<Ride> ridesInRange = redblacktree.searchRange(rideNumber1, rideNumber2);
        if(ridesInRange.size()>0){
            for(int i=0;i<ridesInRange.size()-1;i++) {
                Ride ride = ridesInRange.get(i);
                System.out.print("(" + ride.rideNumber + "," + ride.rideCost + "," + ride.tripDuration + "),");
            }
            Ride finalRide = ridesInRange.get(ridesInRange.size()-1);
            System.out.println("(" + finalRide.rideNumber + "," + finalRide.rideCost + "," + finalRide.tripDuration + ")");
        }else{
            System.out.println("(0,0,0)");
        }
    }

    void Insert(int rideNumber, int rideCost, int tripDuration){ //log(n)
        //If ride is already present, exit else insert ride into redblack tree, heap and add an entry in the map
        if(ridemap.containsKey(rideNumber)){
            System.out.println("Duplicate RideNumber");
            System.exit(0);
        }

        Ride ride = new Ride(rideNumber, rideCost, tripDuration);
        minheap.insert(ride);
        ridemap.put(rideNumber, ride);
        Node node = new Node(rideNumber,ride);
        redblacktree.insert(node);
    }

    public void GetNextRide() {//log(n)
        //If heap is empty, means there are no more rides
        if(minheap.isEmpty()){
            System.out.println("No active ride requests");
            return;
        }
        //get the ride from min heap and delete that ride from the minHeap and redBlack tree
        Ride ride = minheap.removeMin();
        System.out.println("(" + ride.rideNumber + "," + ride.rideCost+ "," + ride.tripDuration + ")");
        redblacktree.deleteNode(ride.rideNumber);
        minheap.delete(ride.rideNumber);
        ridemap.remove(ride.rideNumber);
    }

    public void CancelRide(int rideNumber) {//log(n)
        //To cancel a ride, search the ride and if present delete it fron redblack tree, minHeap and rideMap entry
        if(ridemap.containsKey(rideNumber)){
            redblacktree.deleteNode(rideNumber);
            minheap.delete(rideNumber);
            ridemap.remove(rideNumber);
        } else{
            return;
        }   
    }

    public void UpdateTrip(int rideNumber, int newTripDuration) {//log(n)
        //search the ride in redBlack tree and update accoring to given conditions
        if(!ridemap.containsKey(rideNumber)){
            return;
        }

        Node node = redblacktree.search(rideNumber);
        Ride ride = node.ride;

        //if the new_tripDuration <= existing tripDuration, there would be no action needed 
        //(not updating heap here)
        if (newTripDuration <= ride.tripDuration) {
            ride.tripDuration = newTripDuration;
            return;
        } 
        //if the existing_tripDuration < new_tripDuration <= 2*(existing tripDuration), cost penalty of 10
        else if (ride.tripDuration < newTripDuration && newTripDuration <= 2 * ride.tripDuration) {
            redblacktree.deleteNode(rideNumber);
            minheap.delete(rideNumber);
            ridemap.remove(rideNumber);
            Insert(rideNumber, ride.rideCost+10, newTripDuration);
        } 
        //if the new_tripDuration > 2*(existing tripDuration), the ride id deleted
        else {
            redblacktree.deleteNode(rideNumber);
            minheap.delete(rideNumber);
            ridemap.remove(rideNumber);
        }
        
    }

}