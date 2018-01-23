# Tools

A collection of tools to help debug or setup the development.

## Usage

Fire up an ElasticSearch node:

```
docker run -d -p 9200:9200 -p 9300:9300 -e "discovery.type=single-node" docker.elastic.co/elasticsearch/elasticsearch:6.1.2
```

You can run the GenomeIndexer by running:

```
tools/run --file=/home/fabian/test.fa --host=http://localhost:9000
 ```

Search for the uploaded sequences:

```
curl 'localhost:9200/genome/_search?pretty' -H "Content-Type: application/json" -d '                                       1285
{
    "query" : {
          "match_all" : {}
    }    
}' | less
```
