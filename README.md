# The Web App
### CSCI 3601 F19 Iteration 3
##### Authors: Kedrick Hill, Tyler Rowland, Matthew Spilman, David Escudero, Hoomz Damte, and Emma Oswood

[![Build Status](https://travis-ci.org/UMM-CSci-3601-F19/iteration-3-the-groovy-gravies.svg?branch=master)](https://travis-ci.org/UMM-CSci-3601-F19/iteration-3-the-groovy-gravies)

## What is Morris Laundry Facilities?

### Overview
Morris Laundry Facilities is a Web Application that assists students (users) that are on campus with checking laundry 
without having to go to the room. The App allows users to check how many machines are free in a building of their
choosing, unless they are not picky, which they then can select from the entire list of machines. 

Each machine, whether dryer or washer, has a card containing its information. Clicking more details allows the user
to view additional information and options such as reporting the machine.

The graph, at the bottom of the page, shows historical information about the total usage of machines during specific 
times of the day and the week using chart.js. Graph can be changed from bar graph to line graph and vice versa.

### When selecting a room

Users have the option to see what machines are available, running, or broken for both types (Washers and Dryers).

Users can also favorite a room if they prefer to by clicking on the heart icon. This saves the room for later when
the user returns to the page.



## Technologies Used
### Angular
- [Angular documentation][angular]
- [TypeScript documentation][typescript-doc]
- [What _is_ Angular CLI?][angular-cli]
- [What are environments in Angular CLI?][environments]
- [Testing Angular with Karma/Jasmine][angular5-karma-jasmine]
- [End to end testing (e2e) with protactor and Angular CLI][e2e-testing]
- [Angular CLI commands](https://github.com/angular/angular-cli/wiki)
- [Angular Material Design][angular-md]

### SparkJava
- [Spark documentation][spark-documentation]
- [HTTP Status Codes][status-codes]
- [Other Resources][lab2]

### MongoDB
- [Mongo's Java Drivers (Mongo JDBC)][mongo-jdbc]

### Other Tools
- [Travis CI][travis]
- [Bootstrap][bootstrap]
- [Jasmine and Karma][angular5-karma-jasmine]

[angular-md]: https://material.angular.io/
[angular-cli]: https://angular.io/cli
[typescript-doc]: https://www.typescriptlang.org/docs/home.html
[angular]: https://angular.io/docs
[angular5-karma-jasmine]: https://codecraft.tv/courses/angular/unit-testing/jasmine-and-karma/
[e2e-testing]: https://coryrylan.com/blog/introduction-to-e2e-testing-with-the-angular-cli-and-protractor
[environments]: http://tattoocoder.com/angular-cli-using-the-environment-option/
[bootstrap]: https://getbootstrap.com/components/
[spark-documentation]: http://sparkjava.com/documentation.html
[status-codes]: https://en.wikipedia.org/wiki/List_of_HTTP_status_codes
[lab2]: https://github.com/UMM-CSci-3601/3601-lab2_client-server/blob/master/README.md#resources
[mongo-jdbc]: https://docs.mongodb.com/ecosystem/drivers/java/
[travis]: https://travis-ci.org/
