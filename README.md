[![Build Status](https://travis-ci.org/peri4n/bIO.svg?branch=master)](https://travis-ci.org/peri4n/bIO)

# bIO - A search engine for sequence data

The bIO project aims to provide a search engine for sequence data (Imagine googling your genome). We are 
perfectly aware of tools like BLAST allow you to search through giant data sets, but these tools have there 
problems:

- They do not scale - at least in a Big Data sense
- They are not easily set up for private use
- They lack a good user interface

We try to solve all of there problems using modern approaches and best practices.

## Quick Overview

At its heart bIO is a web application. It uses the play framework in it's backend and react in the front end.

The project is structured into different subprojects to enforce a clean separation of code between the 
different layers:

- `webapp` contains all the logic associated with the web application (including javascript)
- `codec` contains code to parses the different supported formats

## Getting Stared

To run the application run:

`sbt webapp/run`

## License

Copyright (C) 2009-2017 Fabian Bull (https://www.bioinform.at/)

Licensed under the Apache License, Version 2.0 (the "License"); you may not use this project except in 
compliance with the License. You may obtain a copy of the License at 
http://www.apache.org/licenses/LICENSE-2.0.

Unless required by applicable law or agreed to in writing, software distributed under the License is 
distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See 
the License for the specific language governing permissions and limitations under the License.
