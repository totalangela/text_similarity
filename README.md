Text Similarity Application Design and Implementation
Angela Li (UC Berkeley ‘24)
 
Synopsis

I created the text similarity application project which hosts a Jetty web server and renders web UI. User can enter text input in a text box and press the compare button or simply press the enter key to get similarity report which is mainly about the similarity score of the user input text and the content on the Internet. User input is passed from front end to the backend via Ajax. The backend receives the user input text, calls Brave Search Engine API to search the Internet based on the input text, extracts the URL from the search results, retrieves the content from each of the URL and does the comparison using Cosine similarity via Apache Commons Math API. The backend highlights the words in the input text whose similarity score is above 0.5 and returns the result to the front end to be displayed in tabular format.

How to compile the source code from windows machine

Unzip the source code (rumi.zip as attached), add your Brave Search Engine API key in the  brave_search_api_key file in src/main/resources directory and open a command prompt and cd to the project root directory and run the following command:

mvn clean package

How to run the application

cd to the project root directory and run the following command:

mvn jetty:run
How to test the application

The web UI can be accessed at http://localhost:8080/rumi/ and looks like below.
   



Architecture

The design is based on MVC which consists of data models, business logic controllers and views. The front end uses jQuery and Ajax for better user experience.
   
 
 
 
 
Design pattern

I used dependency injection to inject the WebSearcher and JsonParser in TextComparator to separate the concerns of constructing objects and facilitate code re-use.

Performance

As I am using a free Brave Search API account hence can’t parallel the search of the user input text across the multiple paging which makes the data processing somewhat slow. To not slow down the UI too much, I have limited the search to be only 1 page for now. The number of paging can be updated via application.properties file.

Accuracy

The accuracy of the resulting text similarity between user input and content on the Internet is largely depending on the search result quality of Brave Search API. I have observed Brave Search API does not return the search result expected from Google or Bing. Also, Brave search API returns HTTP 422 for long text search (e.g., 470 characters). Not sure it is related to the free test account I signed up with.

Error handling
The project has basic web UI validation against no data input.


Unit test

I created 2 simple junit test classes which test TextComparator class and JsonParser class. 
OS tested
Windows 11 Home 64 bit