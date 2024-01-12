Text Similarity Application Design and Implementation
 
Angela Li (UC Berkeley ‘24)

![rumi](https://github.com/totalangela/text_similarity/assets/76748709/2302aa6d-d0bc-423e-b8d6-4bb3f87b6199)

**Overview**

I created a text similarity application hosted on a Jetty web server and renders web UI. Users can enter text in a text box and press the compare button or simply press the enter key to get the similarity report, which provides in tabular format the similarity score of the user input text, the highlighted matching content on the Internet, and the similarity score on a scale from 0 to 1. User input is passed from front end to the backend via Ajax. The backend receives the user input text, calls Brave Search Engine API to search the Internet based on the input text, extracts the URL from the search results using a JSON Parser that I created, retrieves the content from each of the URL and compares using Cosine similarity via Apache Commons Math API. The backend highlights the words in the input text whose similarity score is above 0.5 and returns the result to the front end.


**How to compile the source code from windows machine**

Unzip the source code (rumi.zip as attached), add your Brave Search Engine API key in the  brave_search_api_key file in src/main/resources directory and open a command prompt and cd to the project root directory and run the following command:

mvn clean package


**How to run the application**

cd to the project root directory and run the following command:

mvn jetty:run


**How to test the application**

The web UI can be accessed at http://localhost:8080/rumi/


**Architecture**

The design is based on MVC which consists of data models, business logic controllers and views. The front end uses jQuery and Ajax for better user experience.
   
 
**Design pattern**

I used dependency injection to inject the WebSearcher and JsonParser in TextComparator to separate the concerns of constructing objects and facilitate code re-use.


**Performance**

As I am using a free Brave Search API account, I can’t allow searching across multiple pages because it makes the data processing somewhat slow. To not slow down the UI too much, I have limited the search to be only 1 page for now. The number of paging can be updated via application.properties file.


**Accuracy**

The accuracy of the resulting text similarity between user input and content on the Internet is largely depending on the search result quality of Brave Search API. I have observed Brave Search API does not return the search result expected from Google or Bing. Also, Brave search API returns HTTP 422 for long text search (e.g., 470 characters). Not sure it is related to the free test account I signed up with. However, I have tested many different sentence blurbs across news websites and have gotten the accurate URL as the first link in my result. You can test yourself and see if my app will give you the accurate link.


**Error handling**

The project has basic web UI validation against no data input.


**Unit test**

I created 2 simple junit test classes which test TextComparator class and JsonParser class. 


**OS tested**

Windows 11 Home 64 bit
