![Java Build](https://github.com/ouvreboite/velib_streaming/workflows/Java%20Build/badge.svg)
# Velib streaming

A java project using Kafka to process the current status of Paris Velib (bicycles) stations and diplay them using OpenLayers

## Description

Using the Paris' [OpenData API](https://opendata.paris.fr/) as its source, this application use a Kafka pipeline to rank the Velib stations by their nearby traffic (using the counters in the city). Using windowed stream, it also try to detected "locked" slots and stations. The aggregated data is displayed in a simple web application. 

Four dataset are used :
* [velib-disponibilite-en-temps-reel](https://opendata.paris.fr/explore/dataset/velib-disponibilite-en-temps-reel) : the current (near real time) status of each station (available bicycles of each type, status of the station, ...)
* [velib-emplacement-des-stations](https://opendata.paris.fr/explore/dataset/velib-emplacement-des-stations) : each station characteristics (name, geoloc, ...)
* [comptage-velo-donnees-compteurs](https://opendata.paris.fr/explore/dataset/comptage-velo-donnees-compteurs) : the daily number of bicycle counted by each existing monitoring site for the past 13 months
* [comptage-velo-compteurs](https://opendata.paris.fr/explore/dataset/comptage-velo-compteurs) : each bicyle counter characteristics (name, geoloc, ...)

High level architecture :

![High level architecture](docs/plantuml/container_diagram.svg)

## Structure
* docs : resources for documentation (schemas, ...)
* run : docker compose file to setup the local env
* source : java application

## Prerequisites

* Docker for setting up the local env
* Java SDK (>=11) to run the application
* Maven for the dependencies management

## Running

TODO

## License

This project is licensed under the MIT License - see the [LICENSE.md](LICENSE.md) file for details

## Acknowledgments

* This project was inspired by Udacity's Datastreaming course.
