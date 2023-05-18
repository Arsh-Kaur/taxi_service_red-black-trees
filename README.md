# taxi_service_red-black-trees

## Problem description 
GatorTaxi is an up-and-coming ride-sharing service. They get many ride requests every day and are planning to develop new software to keep track of their pending ride requests.
A ride is identified by the following triplet:
rideNumber: unique integer identifier for each ride. rideCost: The estimated cost (in integer dollars) for the ride. tripDuration: the total time (in integer minutes) needed to get from pickup to destination.
The needed operations are
1. Print(rideNumber) prints the triplet (rideNumber, rideCost, tripDuration).
2. Print(rideNumber1, rideNumber2) prints all triplets (rx, rideCost, tripDuration) for which rideNumber1 <= rx <= rideNumber2.
3. Insert (rideNumber, rideCost, tripDuration) where rideNumber differs from existing ride numbers.
4. GetNextRide() When this function is invoked, the ride with the lowest rideCost (ties are broken by selecting the ride with the lowest tripDuration) is output. This ride is then deleted from the data structure.
5. CancelRide(rideNumber) deletes the triplet (rideNumber, rideCost, tripDuration) from the data structures, can be ignored if an entry for rideNumber doesn’t exist.
6. UpdateTrip(rideNumber, new_tripDuration) where the rider wishes to change the destination, in this case, \
a) if the new_tripDuration <= existing tripDuration, there would be no action needed. \
b) if the existing_tripDuration < new_tripDuration <= 2*(existing tripDuration), the driver will cancel the existing ride and a new ride request would be created with a penalty of 10 on existing rideCost . We update the entry in the data structure with (rideNumber, rideCost+10, new_tripDuration) \
c) if the new_tripDuration > 2*(existing tripDuration), the ride would be automatically declined and the ride would be removed from the data structure. \
\
In order to complete the given task, you must use a min-heap and a Red-Black Tree (RBT). You must write your own code for the min heap and RBT. Also, you may assume that the number of active rides will not exceed 2000.
A min heap should be used to store (rideNumber, rideCost, tripDuration) triplets ordered by rideCost. \
If there are multiple triplets with the same rideCost, the one with the shortest tripDuration will be given higher priority (given all rideCost-tripDuration sets will be unique). An RBT should be used to store (rideNumber, rideCost, tripDuration) triplets ordered by rideNumber. You are required to maintain pointers between corresponding nodes in the min-heap and RBT.
GatorTaxi can handle only one ride at a time. When it is time to select a new ride request, the ride with the lowest rideCost(ties are broken by selecting the ride with the lowest tripDuration) is selected (Root node in min heap). When no rides remain, return a message “No active ride requests”.

## Input Format
Input test data will be given in the following format.
Insert(rideNumber, rideCost, tripDuration)
Print(rideNumber)
Print (rideNumber1,rideNumber2)
UpdateTrip(rideNumber, newTripDuration)
GetNextRide()
CancelRide(rideNumber)

### Example 1:
Insert(25,98,46) \
GetNextRide() \
GetNextRide() \
Insert(42,17,89) \
Insert(9,76,31) \
Insert(53,97,22) \
GetNextRide() \
Insert(68,40,51) \
GetNextRide() \
Print(1,100) \
UpdateTrip(53,15) \
Insert(96,28,82) \
Insert(73,28,56) \
UpdateTrip(9,88) \
GetNextRide() \
Print(9) \
Insert(20,49,59) \
Insert(62,7,10) \
CancelRide(20) \
Insert(25,49,46) \
UpdateTrip(62,15) \
GetNextRide() \
Print(1,100) \
Insert(53,28,19) \
Print(1,100) \
\
The output for this would be: \
(25,98,46) \
No active ride requests \
(42,17,89) \
(68,40,51) \
(9,76,31),(53,97,22) \
(73,28,56) \
(0,0,0) \
(62,17,15) \
(25,49,46),(53,97,15),(96,28,82) \
Duplicate RideNumber \
\
Print(rideNumber) query should be executed in O(log(n)) and Print(rideNumber1,rideNumber2) should be executed in O(log(n)+S) where n is the number of active rides and S is the number of triplets printed. For this, your search of the RBT should enter only those subtrees that may possibly have a ride in the specified range. All other operations should take O(log(n)) time.
