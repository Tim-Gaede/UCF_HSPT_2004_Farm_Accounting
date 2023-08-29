#include <iostream>
#include <fstream>
#include <map>
#include <string>
#include <cctype>

using namespace std;

// A struct to store information about crops. 
typedef struct crop {
  std::string name;
  char symbol;
  char type;
  int patches;
  int cost;
  int sellPrice;
  int firstHarvest;
  int nextHarvests;
} crop;

// Utility function to find the first capitalized character in a string.
// Doesn't work if there is none. For the purposes of this problem, this
// won't come up. 
char get_symbol(const std::string &cropName) {
  int i = 0;
  while (!isupper(cropName[i]))
    i++;
  return cropName[i];
}

int main() {
  ifstream in("farm.in");
  int count = 0;

  while (true) {
    // Since STL associative collections are all sorted on the key values, they can
    // be used as event queues, using the simulation time as the key. 
    // This has to be a multimap, since there can be multiple events at the same time. 
    multimap<int, crop> eventQueue;
  
    int seasonLength, nCrops;
    
    // Input the header and check for terminating condition.
    in >> seasonLength;
    if (seasonLength == 0)
      break;
    in >> nCrops;

    printf("Season %i\n", ++count);
    printf("+---+----------+----------+--------+--------+\n");
    printf("|Day|  Plant   | Harvest  |Expenses| Income |\n");
    printf("+---+----------+----------+--------+--------+\n");

    // Deal with the first day output while reading the input. 
    int totalIncome = 0, totalExpenses = 0;
    string firstDay;
    for (int i = 0; i < nCrops; i++) {
      crop c;
      in >> c.name >> c.type >> c.patches >> c.cost >> c.sellPrice >> c.firstHarvest;
      if (c.type == 'M')
	in >> c.nextHarvests;
      c.symbol = get_symbol(c.name);
      // Only grow the crop if there is time in the season to harvest it.
      if (c.firstHarvest + 1 <= seasonLength) {
	firstDay.push_back(c.symbol);
	totalExpenses += c.cost * c.patches;
	// Add an event firstHarvest days after the first day. 
	eventQueue.insert(make_pair(c.firstHarvest+1, c));
      }
    } 
    
    // Special case---no crops, nothing to be printed here.
    if (nCrops > 0 && !firstDay.empty())
      printf("|  1|%-10s|          |%7iG|%7iG|\n", firstDay.c_str(), totalExpenses, 0);

    // Now the main event loop. 
    while (true) {

      // Check if the season is over. 
      if (eventQueue.empty() || eventQueue.begin()->first > seasonLength)
	break;

      
      string harvest, plant;
      int expenses = 0, income = 0;

      // Get the next day on which an event happens. Skip all the boring days. 
      int day = eventQueue.begin()->first;
      
      // Remove and process all events on that day. 
      while (!eventQueue.empty() && eventQueue.begin()->first == day) {
	crop c = eventQueue.begin()->second;
	eventQueue.erase(eventQueue.begin());

	// Don't forget - nine crops to a patch. 
	income += c.sellPrice * c.patches * 9;

	// Add the next event, and add the expense of replanting if necessary.
	if (c.type == 'M') {
	  eventQueue.insert(make_pair(day + c.nextHarvests, c));
	  harvest += c.symbol;
	}
	else {
  	  harvest += c.symbol;

	  // Only replant if the harvest will come before the end of the season.
	  if (day + c.firstHarvest <= seasonLength) {
	    eventQueue.insert(make_pair(day + c.firstHarvest, c));
	    expenses += c.cost * c.patches;
	    plant += c.symbol;
	  }
	}
      }
      totalExpenses += expenses;
      totalIncome += income;


      printf("|%3i|%-10s|%-10s|%7iG|%7iG|\n", day, plant.c_str(), harvest.c_str(), expenses, income);
    }
    printf("+---+----------+----------+--------+--------+\n");
    printf("|ALL|          |          |%7iG|%7iG|\n", totalExpenses, totalIncome);
    printf("+---+----------+----------+--------+--------+\n");
    printf("Ali will make %iG.\n\n", totalIncome - totalExpenses);
  }
}
