

[TOC]



## 基础入门



### company index lib

```shell
{
	"employee": {
		"dynamic": "false",
		"properties": {
			"first_name": {
				"type": "text",
				"fields": {
					"keyword": {
						"type": "keyword",
						"ignore_above": 256
					}
				}
			},
			"about": {
				"type": "text",
				"analyzer": "ik_max_word",
				"search_analyzer": "ik_smart",
				"fields": {
					"keyword": {
						"type": "keyword",
						"ignore_above": 256
					}
				}
			},
			"last_name": {
				"type": "text",
				"fields": {
					"keyword": {
						"type": "keyword",
						"ignore_above": 256
					}
				}
			},
			"age": {
				"type": "long"
			},
			"lucky_num": {
				"type": "long"
			},
			"interests": {
				"type": "text",
				"fields": {
					"keyword": {
						"type": "keyword",
						"ignore_above": 256
					}
				}
			}
		}
	}
}
```



#### 索引文档

```shell
curl -i -XPUT '10.250.140.14:9200/alibaba_alias/employee/1?pretty' -d '{
    "first_name" : "John",
    "last_name" :  "Smith",
	"lucky_num" :   [ 8, 6 ],
    "age" :        25,
    "about" :      "I love to go rock climbing",
    "interests": [ "sports", "music" ]
}'


curl -i -XGET '10.250.140.14:9200/alibaba_alias/employee/1?pretty'


curl -i -XPUT '10.250.140.14:9200/alibaba_alias/employee/2?pretty' -d '{
    "first_name" : "科比",
    "last_name" :  "布莱恩特",
	"lucky_num" :   [ 9, 66 ],
    "age" :        33,
    "about" :      "我喜欢爬山和旅游。",
    "interests": [ "篮球", "听音乐" ]
}'


curl -i -XPUT '10.250.140.14:9200/alibaba_alias/employee/3?pretty' -d '{
    "first_name" : "詹姆斯",
    "last_name" :  "哈登",
	"lucky_num" :   [ 33, 77, 55 ],
    "age" :        23,
    "about" :      "用心做自己，永不言败。",
    "interests": [ "篮球", "逛街" ]
}'



curl -XPUT '10.250.140.14:9200/alibaba_alias/employee/4?pretty' -H 'Content-Type: application/json' -d '{
    "first_name" :  "Jane",
    "last_name" :   "Smith",
    "lucky_num" :   [ 16, 22, 7 ],
    "age" :         32,
    "about" :       "I like to collect rock albums",
    "interests":  [ "music" ]
}'

curl -XPUT '10.250.140.14:9200/alibaba_alias/employee/5?pretty' -H 'Content-Type: application/json' -d '{
    "first_name" :  "Douglas",
    "last_name" :   "Fir",
    "lucky_num" :   [ 3, 87, 32 ],
    "age" :         35,
    "about":        "I like to build cabinets",
    "interests":  [ "forestry" ]
}'


curl -i -XGET '10.250.140.14:9200/alibaba_alias/employee/_search?pretty'

----------------------------------

curl -XGET 'http://10.250.140.14:9200/alibaba_alias/employee/_search?pretty' -d '
{
	"query" : {
		"match" : {
			"last_name" : "布莱恩"
		}
	},
    "from": 0, 
    "size": 10
}'

{
  "took" : 2,
  "timed_out" : false,
  "_shards" : {
    "total" : 3,
    "successful" : 3,
    "failed" : 0
  },
  "hits" : {
    "total" : 1,
    "max_score" : 0.8630463,
    "hits" : [
      {
        "_index" : "alibaba",
        "_type" : "employee",
        "_id" : "2",
        "_score" : 0.8630463,
        "_source" : {
          "first_name" : "科比",
          "last_name" : "布莱恩特",
          "lucky_num" : [
            9,
            66
          ],
          "age" : 33,
          "about" : "我喜欢爬山和旅游。",
          "interests" : [
            "篮球",
            "听音乐"
          ]
        }
      }
    ]
  }
}



curl -XGET 'http://10.250.140.14:9200/alibaba_alias/employee/_search?pretty' -d '
{
	"query" : {
		"term" : {
			"last_name" : "布莱恩"
		}
	},
    "from": 0, 
    "size": 10
}'

{
  "took" : 1,
  "timed_out" : false,
  "_shards" : {
    "total" : 3,
    "successful" : 3,
    "failed" : 0
  },
  "hits" : {
    "total" : 0,
    "max_score" : null,
    "hits" : [ ]
  }
}


# 如果只想检查一个文档是否存在 --根本不想关心内容--那么用 HEAD 方法来代替 GET 方法。
# HEAD 请求没有返回体，只返回一个 HTTP 请求报头
curl -i -XHEAD http://10.250.140.14:9200/alibaba_alias/employee/1
```



#### 查询

```shell
curl -XGET '10.250.140.14:9200/alibaba_alias/employee/_search?pretty'


# 查询字符串 （_query-string_） 搜索
# 10.250.140.14:9200/alibaba_alias/employee/_search?q=last_name:哈登&pretty
# 中文要编码
curl -XGET 'http://10.250.140.14:9200/alibaba_alias/employee/_search?q=last_name:%E5%93%88%E7%99%BB&pretty'


# 查询表达式搜索
curl -XGET '10.250.140.14:9200/alibaba_alias/employee/_search?pretty' -H 'Content-Type: application/json' -d '
{
    "query" : {
        "match" : {
            "last_name" : "哈登"
        }
    }
}'

```






#### 过滤器

```shell
curl -XGET '10.250.140.14:9200/alibaba_alias/employee/_search?pretty' -H 'Content-Type: application/json' -d '
{
    "query" : {
        "bool": {
            "must": {
                "match" : {
                    "last_name" : "smith" 
                }
            },
            "filter": {
                "range" : {
                    "age" : { "gt" : 20 } 
                }
            }
        }
    }
}'

```



#### [全文搜索](https://www.elastic.co/guide/cn/elasticsearch/guide/current/_full_text_search.html)

```shell
curl -XGET '10.250.140.14:9200/alibaba_alias/employee/_search?pretty' -H 'Content-Type: application/json' -d'
{
    "query" : {
        "match" : {
            "about" : "rock climbing"
        }
    }
}
'


```



#### [短语搜索](https://www.elastic.co/guide/cn/elasticsearch/guide/current/_phrase_search.html)

```shell
curl -XGET '10.250.140.14:9200/alibaba_alias/employee/_search?pretty' -H 'Content-Type: application/json' -d'
{
    "query" : {
        "match_phrase" : {
            "about" : "rock climbing"
        }
    }
}
'

```



#### [高亮搜索](https://www.elastic.co/guide/cn/elasticsearch/guide/current/highlighting-intro.html)

```shell
curl -XGET '10.250.140.14:9200/alibaba_alias/employee/_search?pretty' -H 'Content-Type: application/json' -d'
{
    "query" : {
        "match_phrase" : {
            "about" : "rock climbing"
        }
    },
    "highlight": {
        "fields" : {
            "about" : {}
        }
    }
}
'

```



#### [分析](https://www.elastic.co/guide/cn/elasticsearch/guide/current/_analytics.html)

```shell
curl -XGET '10.250.140.14:9200/alibaba_alias/employee/_search?pretty' -H 'Content-Type: application/json' -d'
{
  "size": 0,
  "aggs": {
    "all_interests": {
      "terms": { "field": "interests.keyword" }
    }
  }
}
'


----------------
# 聚合并非预先统计，而是从匹配当前查询的文档中即时生成。
{
  "took" : 4,
  "timed_out" : false,
  "_shards" : {
    "total" : 3,
    "successful" : 3,
    "failed" : 0
  },
  "hits" : {
    "total" : 5,
    "max_score" : 0.0,
    "hits" : [ ]
  },
  "aggregations" : {
    "all_interests" : {
      "doc_count_error_upper_bound" : 0,
      "sum_other_doc_count" : 0,
      "buckets" : [
        {
          "key" : "music",
          "doc_count" : 2
        },
        {
          "key" : "篮球",
          "doc_count" : 2
        },
        {
          "key" : "forestry",
          "doc_count" : 1
        },
        {
          "key" : "sports",
          "doc_count" : 1
        },
        {
          "key" : "听音乐",
          "doc_count" : 1
        },
        {
          "key" : "逛街",
          "doc_count" : 1
        }
      ]
    }
  }
}


# Smith 的雇员中最受欢迎的兴趣爱好，可以直接添加适当的查询来组合查询
curl -XGET '10.250.140.14:9200/alibaba_alias/employee/_search?pretty' -H 'Content-Type: application/json' -d'
{
  "query": {
    "match": {
      "last_name": "smith"
    }
  },
  "aggs": {
    "all_interests": {
      "terms": {
        "field": "interests.keyword"
      }
    }
  }
}
'



# 聚合还支持分级汇总 。比如，查询特定兴趣爱好员工的平均年龄：
curl -XGET '10.250.140.14:9200/alibaba_alias/employee/_search?pretty' -H 'Content-Type: application/json' -d'
{
    "aggs" : {
        "all_interests" : {
            "terms" : { "field" : "interests.keyword" },
            "aggs" : {
                "avg_age" : {
                    "avg" : { "field" : "age" }
                }
            }
        }
    }
}
'


```



#### [集群健康](https://www.elastic.co/guide/cn/elasticsearch/guide/current/cluster-health.html)

status 字段指示着当前集群在总体上是否工作正常。它的三种颜色含义如下：

green
所有的主分片和副本分片都正常运行。
yellow
所有的主分片都正常运行，但不是所有的副本分片都正常运行。
red
有主分片没能正常运行。

```shell
curl -XGET '10.250.140.14:9200/_cluster/health?pretty'

{
  "cluster_name" : "es-cluster",
  "status" : "yellow",
  "timed_out" : false,
  "number_of_nodes" : 1,
  "number_of_data_nodes" : 1,
  "active_primary_shards" : 52,
  "active_shards" : 52,
  "relocating_shards" : 0,
  "initializing_shards" : 0,
  "unassigned_shards" : 52,
  "delayed_unassigned_shards" : 0,
  "number_of_pending_tasks" : 0,
  "number_of_in_flight_fetch" : 0,
  "task_max_waiting_in_queue_millis" : 0,
  "active_shards_percent_as_number" : 50.0
}

```



```shell
# 设置分片和副本数
curl -XPUT '10.250.140.14:9200/blogs?pretty' -H 'Content-Type: application/json' -d'
{
   "settings" : {
      "number_of_shards" : 3,
      "number_of_replicas" : 1
   }
}
'

# 更新副本数
curl -XPUT '10.250.140.14:9200/blogs/_settings?pretty' -H 'Content-Type: application/json' -d'
{
   "number_of_replicas" : 2
}
'

```



#### 数据输入和输出

```shell
# 创建新文档
curl -XPUT '10.250.140.14:9200/website/blog/123?pretty' -H 'Content-Type: application/json' -d'
{
  "title": "My first blog entry",
  "text":  "Just trying this out...",
  "date":  "2014/01/01"
}
'

# 自动生成的 ID 是 URL-safe、 基于 Base64 编码且长度为20个字符的 GUID 字符串。 
# 这些 GUID 字符串由可修改的 FlakeID 模式生成，这种模式允许多个节点并行生成唯一 ID ，
# 且互相之间的冲突概率几乎为零。
curl -XPOST '10.250.140.14:9200/website/blog/?pretty' -H 'Content-Type: application/json' -d'
{
  "title": "My second blog entry",
  "text":  "Still trying this out...",
  "date":  "2014/01/01"
}
'

curl -XGET '10.250.140.14:9200/website/blog/123?pretty'

curl -i -XGET http://10.250.140.14:9200/website/blog/124?pretty

# 返回文档的一部分
curl -XGET '10.250.140.14:9200/website/blog/123?_source=title,text&pretty'

{
  "_index" : "website",
  "_type" : "blog",
  "_id" : "123",
  "_version" : 1,
  "found" : true,
  "_source" : {
    "text" : "Just trying this out...",
    "title" : "My first blog entry"
  }
}

# 返回文档_source字段
curl -XGET '10.250.140.14:9200/website/blog/123/_source?pretty'

{
  "title" : "My first blog entry",
  "text" : "Just trying this out...",
  "date" : "2014/01/01"
}

# 检查文档是否存在
curl -i -XHEAD http://10.250.140.14:9200/website/blog/123


# 更新文档
# 在 Elasticsearch 中文档是 不可改变 的，不能修改它们。
# 相反，如果想要更新现有的文档，需要 重建索引 或者进行替换
curl -XPUT '10.250.140.14:9200/website/blog/123?pretty' -H 'Content-Type: application/json' -d'
{
  "title": "My first blog entry",
  "text":  "I am starting to get the hang of this...",
  "date":  "2014/01/02"
}
'
-----------------------------
{
  "_index" : "website",
  "_type" : "blog",
  "_id" : "123",
  "_version" : 2,   @@@
  "result" : "updated",  @@@
  "_shards" : {
    "total" : 2,
    "successful" : 1,
    "failed" : 0
  },
  "created" : false  @@@
}


# 删除文档
curl -XDELETE '10.250.140.14:9200/website/blog/123?pretty'


```



#### 创建新文档

使用索引请求的 `POST` 形式让 Elasticsearch 自动生成唯一 `_id` :

```shell
POST /website/blog/
{ ... }
```



如果已经有自己的 `_id` ，那么我们必须告诉 Elasticsearch ，只有在相同的 `_index` 、 `_type` 和 `_id` 不存在时才接受我们的索引请求。这里有两种方式，他们做的实际是相同的事情。

第一种方法使用 `op_type` 查询 -字符串参数：

```
PUT /website/blog/123?op_type=create
{ ... }
```

第二种方法是在 URL 末端使用 `/_create` :

```
PUT /website/blog/123/_create
{ ... }
```

如果创建新文档的请求成功执行，Elasticsearch 会返回元数据和一个 `201 Created` 的 HTTP 响应码。

另一方面，如果具有相同的 `_index` 、 `_type` 和 `_id` 的文档已经存在，Elasticsearch 将会返回 `409 Conflict` 响应码

```shell
curl -XPUT '10.250.140.14:9200/website/blog/123?pretty=true&op_type=create' -H 'Content-Type: application/json' -d'
{
  "title": "My first blog entry",
  "text":  "I am starting to get the hang of this...",
  "date":  "2014/01/02"
}
'

curl -XPUT '10.250.140.14:9200/website/blog/123/_create?pretty' -H 'Content-Type: application/json' -d'
{
  "title": "My first blog entry",
  "text":  "I am starting to get the hang of this...",
  "date":  "2014/01/02"
}
'
```



#### [乐观并发控制](https://www.elastic.co/guide/cn/elasticsearch/guide/current/optimistic-concurrency-control.html)

```shell
curl -XPUT '10.250.140.14:9200/website/blog/1/_create?pretty' -H 'Content-Type: application/json' -d'
{
  "title": "My first blog entry",
  "text":  "Just trying this out..."
}
'

curl -XGET '10.250.140.14:9200/website/blog/1?pretty'


curl -XPUT '10.250.140.14:9200/website/blog/1?version=1&pretty' -H 'Content-Type: application/json' -d'
{
  "title": "My first blog entry",
  "text":  "Starting to get the hang of this..."
}
'


curl -XPUT '10.250.140.14:9200/website/blog/1?version=1&pretty' -H 'Content-Type: application/json' -d'
{
  "title": "My first blog entry",
  "text":  "Starting to get the hang of this..."
}
'


###################################
# 外部系统使用版本控制  指定版本号
# 当前版本号小于指定版本号
curl -XPUT '10.250.140.14:9200/website/blog/2?version=5&version_type=external&pretty' -H 'Content-Type: application/json' -d'
{
  "title": "My first external blog entry",
  "text":  "Starting to get the hang of this..."
}
'

curl -XPUT '10.250.140.14:9200/website/blog/2?version=10&version_type=external&pretty' -H 'Content-Type: application/json' -d'
{
  "title": "My first external blog entry",
  "text":  "This is a piece of cake..."
}
'


curl -XPUT '10.250.140.14:9200/website/blog/2?version=10&version_type=external&pretty' -H 'Content-Type: application/json' -d'
{
  "title": "My first external blog entry",
  "text":  "This is a piece of cake..."
}
'

```



#### 文档部分更新

```shell
curl -XGET '10.250.140.14:9200/website/blog/1?pretty'

{
  "_index" : "website",
  "_type" : "blog",
  "_id" : "1",
  "_version" : 2,
  "found" : true,
  "_source" : {
    "title" : "My first blog entry",
    "text" : "Starting to get the hang of this..."
  }
}



curl -XPOST '10.250.140.14:9200/website/blog/1/_update?pretty' -H 'Content-Type: application/json' -d'
{
   "doc" : {
      "tags" : [ "testing" ],
      "views": 0
   }
}
'

curl -XGET '10.250.140.14:9200/website/blog/1?pretty'

{
  "_index" : "website",
  "_type" : "blog",
  "_id" : "1",
  "_version" : 3,
  "found" : true,
  "_source" : {
    "title" : "My first blog entry",
    "text" : "Starting to get the hang of this...",
    "views" : 0,
    "tags" : [
      "testing"
    ]
  }
}

# 使用脚本部分更新文档
# 脚本可以在 update API中用来改变 _source 的字段内容， 它在更新脚本中称为 ctx._source 。
curl -XPOST '10.250.140.14:9200/website/blog/1/_update?pretty' -H 'Content-Type: application/json' -d'
{
   "script" : "ctx._source.views+=1"
}
'

curl -XGET '10.250.140.14:9200/website/blog/1?pretty'

{
  "_index" : "website",
  "_type" : "blog",
  "_id" : "1",
  "_version" : 4,
  "found" : true,
  "_source" : {
    "title" : "My first blog entry",
    "text" : "Starting to get the hang of this...",
    "views" : 1,
    "tags" : [
      "testing"
    ]
  }
}


-----------------


curl -XPOST '10.250.140.14:9200/website/blog/1/_update?pretty' -H 'Content-Type: application/json' -d'
{
	"script": {
		"inline": "ctx._source.tags.add(params.new_tag)",
		"params": {
			"new_tag": "search"
		}
	}
}
'


curl -XGET '10.250.140.14:9200/website/blog/1?pretty'
{
  "_index" : "website",
  "_type" : "blog",
  "_id" : "1",
  "_version" : 8,
  "found" : true,
  "_source" : {
    "title" : "My first blog entry",
    "text" : "Starting to get the hang of this...",
    "views" : 0,
    "tags" : [
      "testing",
      "search"
    ]
  }
}


# 选择通过设置 ctx.op 为 delete 来删除基于其内容的文档
curl -XPOST '10.250.140.14:9200/website/blog/1/_update?pretty' -H 'Content-Type: application/json' -d'
{
	"script": {
		"inline": "ctx.op = ctx._source.views == params.count ? \u0027delete\u0027 : \u0027none\u0027",
		"params": {
			"count": 1
		}
	}
}
'

curl -XGET '10.250.140.14:9200/website/blog/1?pretty'
{
  "_index" : "website",
  "_type" : "blog",
  "_id" : "1",
  "found" : false
}


# 更新的文档可能尚不存在
# 使用 upsert 参数，指定如果文档不存在就应该先创建它
# 我们第一次运行这个请求时， upsert 值作为新文档被索引，初始化 views 字段为 1 。 
# 在后续的运行中，由于文档已经存在， script 更新操作将替代 upsert 进行应用，对 views 计数器进行累加。
curl -XPOST '10.250.140.14:9200/website/pageviews/1/_update?pretty' -H 'Content-Type: application/json' -d'
{
   "script" : "ctx._source.views+=1",
   "upsert": {
       "views": 1
   }
}
'

curl -XGET '10.250.140.14:9200/website/pageviews/1?pretty'

# 更新和冲突
# 通过 设置参数 retry_on_conflict 来自动完成， 
# 这个参数规定了失败之前 update 应该重试的次数，它的默认值为 0 。
# 失败之前重试该更新5次。
curl -XPOST '10.250.140.14:9200/website/pageviews/1/_update?retry_on_conflict=5&pretty' -H 'Content-Type: application/json' -d'
{
   "script" : "ctx._source.views+=1",
   "upsert": {
       "views": 0
   }
}
'

# 在增量操作无关顺序的场景，例如递增计数器等这个方法十分有效，
# 但是在其他情况下变更的顺序 是 非常重要的。 类似 index API ， update API 默认采用 
# 最终写入生效 的方案。


```



#### [取回多个文档](https://www.elastic.co/guide/cn/elasticsearch/guide/current/_Retrieving_Multiple_Documents.html)

```shell
curl -XGET '10.250.140.14:9200/_mget?pretty' -H 'Content-Type: application/json' -d '{
   "docs" : [
      {
         "_index" : "website",
         "_type" :  "blog",
         "_id" :    2
      },
      {
         "_index" : "website",
         "_type" :  "pageviews",
         "_id" :    1,
         "_source": "views"
      }
   ]
}'


# 如果想检索的数据都在相同的 _index 中（甚至相同的 _type 中），
# 则可以在 URL 中指定默认的 /_index 或者默认的 /_index/_type 。
curl -XGET '10.250.140.14:9200/website/blog/_mget?pretty' -H 'Content-Type: application/json' -d '{
   "docs" : [
      { "_id" : 2 },
      { "_type" : "pageviews", "_id" :   1 }
   ]
}'

{
  "docs" : [
    {
      "_index" : "website",
      "_type" : "blog",
      "_id" : "2",
      "_version" : 10,
      "found" : true,
      "_source" : {
        "title" : "My first external blog entry",
        "text" : "This is a piece of cake..."
      }
    },
    {
      "_index" : "website",
      "_type" : "pageviews",
      "_id" : "1",
      "_version" : 2,
      "found" : true,
      "_source" : {
        "views" : 2
      }
    }
  ]
}


# 如果所有文档的 _index 和 _type 都是相同的，你可以只传一个 ids 数组，而不是整个 docs 数组
curl -XGET '10.250.140.14:9200/website/blog/_mget?pretty' -H 'Content-Type: application/json' -d '{
   "ids" : [ "2", "1" ]
}'

{
  "docs" : [
    {
      "_index" : "website",
      "_type" : "blog",
      "_id" : "2",
      "_version" : 10,
      "found" : true,
      "_source" : {
        "title" : "My first external blog entry",
        "text" : "This is a piece of cake..."
      }
    },
    {
      "_index" : "website",
      "_type" : "blog",
      "_id" : "1",
      "found" : false
    }
  ]
}


```



#### [代价较小的批量操作](https://www.elastic.co/guide/cn/elasticsearch/guide/current/bulk.html)

```shell
curl -XPOST '10.250.140.14:9200/_bulk?pretty' -H 'Content-Type: application/json' -d'
{ "delete": { "_index": "website", "_type": "blog", "_id": "123" }} 
{ "create": { "_index": "website", "_type": "blog", "_id": "123" }}
{ "title":    "My first blog post" }
{ "index":  { "_index": "website", "_type": "blog" }}
{ "title":    "My second blog post" }
{ "update": { "_index": "website", "_type": "blog", "_id": "123", "_retry_on_conflict" : 3} }
{ "doc" : {"title" : "My updated blog post"} }
'

{
  "took" : 22,
  "errors" : false,
  "items" : [
    {
      "delete" : {
        "found" : false,
        "_index" : "website",
        "_type" : "blog",
        "_id" : "123",
        "_version" : 1,
        "result" : "not_found",
        "_shards" : {
          "total" : 2,
          "successful" : 1,
          "failed" : 0
        },
        "status" : 404
      }
    },
    {
      "create" : {
        "_index" : "website",
        "_type" : "blog",
        "_id" : "123",
        "_version" : 2,
        "result" : "created",
        "_shards" : {
          "total" : 2,
          "successful" : 1,
          "failed" : 0
        },
        "created" : true,
        "status" : 201
      }
    },
    {
      "index" : {
        "_index" : "website",
        "_type" : "blog",
        "_id" : "AWHV9UIF7cHMetqd_3rT",
        "_version" : 1,
        "result" : "created",
        "_shards" : {
          "total" : 2,
          "successful" : 1,
          "failed" : 0
        },
        "created" : true,
        "status" : 201
      }
    },
    {
      "update" : {
        "_index" : "website",
        "_type" : "blog",
        "_id" : "123",
        "_version" : 3,
        "result" : "updated",
        "_shards" : {
          "total" : 2,
          "successful" : 1,
          "failed" : 0
        },
        "status" : 200
      }
    }
  ]
}

# bulk 请求不是原子的： 不能用它来实现事务控制。每个请求是单独处理的，
# 因此一个请求的成功或失败不会影响其他的请求。


curl -XPOST '10.250.140.14:9200/_bulk?pretty' -H 'Content-Type: application/json' -d '
{ "create": { "_index": "website", "_type": "blog", "_id": "123" }}
{ "title":    "Cannot create - it already exists" }
{ "index":  { "_index": "website", "_type": "blog", "_id": "123" }}
{ "title":    "But we can update it" }
'

{
  "took" : 11,
  "errors" : true,
  "items" : [
    {
      "create" : {
        "_index" : "website",
        "_type" : "blog",
        "_id" : "123",
        "status" : 409,
        "error" : {
          "type" : "version_conflict_engine_exception",
          "reason" : "[blog][123]: version conflict, document already exists (current version [3])",
          "index_uuid" : "j-7Dag08SH2hzUv-V3MlYQ",
          "shard" : "0",
          "index" : "website"
        }
      }
    },
    {
      "index" : {
        "_index" : "website",
        "_type" : "blog",
        "_id" : "123",
        "_version" : 4,
        "result" : "updated",
        "_shards" : {
          "total" : 2,
          "successful" : 1,
          "failed" : 0
        },
        "created" : false,
        "status" : 200
      }
    }
  ]
}


# 在 bulk 请求的 URL 中接收默认的 /_index 或者 /_index/_type
curl -XPOST '10.250.140.14:9200/website/_bulk?pretty' -H 'Content-Type: application/json' -d'
{ "index": { "_type": "log" }}
{ "event": "User logged in" }
'

# 仍然可以覆盖元数据行中的 _index 和 _type , 但是它将使用 URL 中的这些元数据值作为默认值
curl -XPOST '10.250.140.14:9200/website/log/_bulk?pretty' -H 'Content-Type: application/json' -d '
{ "index": {}}
{ "event": "User logged in" }
{ "index": { "_type": "blog" }}
{ "title": "Overriding the default type" }
'


```

### [搜索——最基本的工具](https://www.elastic.co/guide/cn/elasticsearch/guide/current/search.html)

```shell
curl -XPUT 'http://localhost:9200/us/user/1?pretty=1' -d '
{
   "email" : "john@smith.com",
   "name" : "John Smith",
   "username" : "@john"
}
'

curl -XPUT 'http://localhost:9200/gb/user/2?pretty=1' -d '
{
   "email" : "mary@jones.com",
   "name" : "Mary Jones",
   "username" : "@mary"
}
'

curl -XPUT 'http://localhost:9200/gb/tweet/3?pretty=1' -d '
{
   "date" : "2014-09-13",
   "name" : "Mary Jones",
   "tweet" : "Elasticsearch means full text search has never been so easy",
   "user_id" : 2
}
'

curl -XPUT 'http://localhost:9200/us/tweet/4?pretty=1' -d '
{
   "date" : "2014-09-14",
   "name" : "John Smith",
   "tweet" : "@mary it is not just text, it does everything",
   "user_id" : 1
}
'

curl -XPUT 'http://localhost:9200/gb/tweet/5?pretty=1' -d '
{
   "date" : "2014-09-15",
   "name" : "Mary Jones",
   "tweet" : "However did I manage before Elasticsearch?",
   "user_id" : 2
}
'

curl -XPUT 'http://localhost:9200/us/tweet/6?pretty=1' -d '
{
   "date" : "2014-09-16",
   "name" : "John Smith",
   "tweet" : "The Elasticsearch API is really easy to use",
   "user_id" : 1
}
'

curl -XPUT 'http://localhost:9200/gb/tweet/7?pretty=1' -d '
{
   "date" : "2014-09-17",
   "name" : "Mary Jones",
   "tweet" : "The Query DSL is really powerful and flexible",
   "user_id" : 2
}
'

curl -XPUT 'http://localhost:9200/us/tweet/8?pretty=1' -d '
{
   "date" : "2014-09-18",
   "name" : "John Smith",
   "user_id" : 1
}
'

curl -XPUT 'http://localhost:9200/gb/tweet/9?pretty=1' -d '
{
   "date" : "2014-09-19",
   "name" : "Mary Jones",
   "tweet" : "Geo-location aggregations are really cool",
   "user_id" : 2
}
'

curl -XPUT 'http://localhost:9200/us/tweet/10?pretty=1' -d '
{
   "date" : "2014-09-20",
   "name" : "John Smith",
   "tweet" : "Elasticsearch surely is one of the hottest new NoSQL products",
   "user_id" : 1
}
'

curl -XPUT 'http://localhost:9200/gb/tweet/11?pretty=1' -d '
{
   "date" : "2014-09-21",
   "name" : "Mary Jones",
   "tweet" : "Elasticsearch is built for the cloud, easy to scale",
   "user_id" : 2
}
'

curl -XPUT 'http://localhost:9200/us/tweet/12?pretty=1' -d '
{
   "date" : "2014-09-22",
   "name" : "John Smith",
   "tweet" : "Elasticsearch and I have left the honeymoon stage, and I still love her.",
   "user_id" : 1
}
'

curl -XPUT 'http://localhost:9200/gb/tweet/13?pretty=1' -d '
{
   "date" : "2014-09-23",
   "name" : "Mary Jones",
   "tweet" : "So yes, I am an Elasticsearch fanboy",
   "user_id" : 2
}
'

curl -XPUT 'http://localhost:9200/us/tweet/14?pretty=1' -d '
{
   "date" : "2014-09-24",
   "name" : "John Smith",
   "tweet" : "How many more cheesy tweets do I have to write?",
   "user_id" : 1
}
'
```



```shell
curl -XPOST 'http://10.250.140.14:9200/_bulk?pretty' -d '
{ "create": { "_index": "us", "_type": "user", "_id": "1" }}
{ "email" : "john@smith.com", "name" : "John Smith", "username" : "@john" }
{ "create": { "_index": "gb", "_type": "user", "_id": "2" }}
{ "email" : "mary@jones.com", "name" : "Mary Jones", "username" : "@mary" }
{ "create": { "_index": "gb", "_type": "tweet", "_id": "3" }}
{ "date" : "2014-09-13", "name" : "Mary Jones", "tweet" : "Elasticsearch means full text search has never been so easy", "user_id" : 2 }
{ "create": { "_index": "us", "_type": "tweet", "_id": "4" }}
{ "date" : "2014-09-14", "name" : "John Smith", "tweet" : "@mary it is not just text, it does everything", "user_id" : 1 }
{ "create": { "_index": "gb", "_type": "tweet", "_id": "5" }}
{ "date" : "2014-09-15", "name" : "Mary Jones", "tweet" : "However did I manage before Elasticsearch?", "user_id" : 2 }
{ "create": { "_index": "us", "_type": "tweet", "_id": "6" }}
{ "date" : "2014-09-16", "name" : "John Smith",  "tweet" : "The Elasticsearch API is really easy to use", "user_id" : 1 }
{ "create": { "_index": "gb", "_type": "tweet", "_id": "7" }}
{ "date" : "2014-09-17", "name" : "Mary Jones", "tweet" : "The Query DSL is really powerful and flexible", "user_id" : 2 }
{ "create": { "_index": "us", "_type": "tweet", "_id": "8" }}
{ "date" : "2014-09-18", "name" : "John Smith", "user_id" : 1 }
{ "create": { "_index": "gb", "_type": "tweet", "_id": "9" }}
{ "date" : "2014-09-19", "name" : "Mary Jones", "tweet" : "Geo-location aggregations are really cool", "user_id" : 2 }
{ "create": { "_index": "us", "_type": "tweet", "_id": "10" }}
{ "date" : "2014-09-20", "name" : "John Smith", "tweet" : "Elasticsearch surely is one of the hottest new NoSQL products", "user_id" : 1 }
{ "create": { "_index": "gb", "_type": "tweet", "_id": "11" }}
{ "date" : "2014-09-21", "name" : "Mary Jones", "tweet" : "Elasticsearch is built for the cloud, easy to scale", "user_id" : 2 }
{ "create": { "_index": "us", "_type": "tweet", "_id": "12" }}
{ "date" : "2014-09-22", "name" : "John Smith", "tweet" : "Elasticsearch and I have left the honeymoon stage, and I still love her.", "user_id" : 1 }
{ "create": { "_index": "gb", "_type": "tweet", "_id": "13" }}
{ "date" : "2014-09-23", "name" : "Mary Jones", "tweet" : "So yes, I am an Elasticsearch fanboy", "user_id" : 2 }
{ "create": { "_index": "us", "_type": "tweet", "_id": "14" }}
{ "date" : "2014-09-24", "name" : "John Smith", "tweet" : "How many more cheesy tweets do I have to write?", "user_id" : 1 }'
```



```shell
curl -XGET '10.250.140.14:9200/_search?pretty'
curl -XGET '10.250.140.14:9200/us/_search?pretty'

curl -XGET '10.250.140.14:9200/us/_search?size=5&pretty'
curl -XGET '10.250.140.14:9200/us/_search?size=5&from=5&pretty'
curl -XGET '10.250.140.14:9200/us/_search?size=5&from=10&pretty'

```

#### [轻量搜索](https://www.elastic.co/guide/cn/elasticsearch/guide/current/search-lite.html)

```shell
# 查询在 tweet 类型中 tweet 字段包含 elasticsearch 单词的所有文档
curl -XGET '10.250.140.14:9200/_all/tweet/_search?q=tweet:elasticsearch&pretty'

# 查询在 name 字段中包含 john 并且在 tweet 字段中包含 mary 的文档
# +name:john +tweet:mary
curl -XGET '10.250.140.14:9200/_search?q=%2Bname%3Ajohn+%2Btweet%3Amary&pretty'

# + 前缀表示必须与查询条件匹配。类似地， - 前缀表示一定不与查询条件匹配。
# 没有 + 或者 - 的所有其他条件都是可选的——匹配的越多，文档就越相关。
```



```shell
# 返回包含 mary 的所有文档
curl -XGET '10.250.140.14:9200/_search?q=mary&pretty'

# 针对tweents类型，并使用以下的条件：
# name 字段中包含 mary 或者 john
# date 值大于 2014-09-10
# _all_ 字段包含 aggregations 或者 geo

# +name:(mary john) +date:>2014-09-10 +(aggregations geo)




```

### [映射和分析](https://www.elastic.co/guide/cn/elasticsearch/guide/current/mapping-analysis.html)

```shell
curl -XGET '10.250.140.14:9200/_search?q=2014              # 12 results&pretty'
curl -XGET '10.250.140.14:9200/_search?q=2014-09-15        # 12 results !&pretty'
curl -XGET '10.250.140.14:9200/_search?q=date:2014-09-15   # 1  result&pretty'
curl -XGET '10.250.140.14:9200/_search?q=date:2014         # 0  results !&pretty'

```

```shell
# 测试分析器
curl -XGET '10.250.140.14:9200/_analyze?pretty' -H 'Content-Type: application/json' -d'
{
  "analyzer": "standard",
  "text": "Text to analyze"
}
'
```



```shell
# 删除
curl -i -XDELETE '10.250.140.14:9200/gb?pretty'

curl -i -XPUT '10.250.140.14:9200/gb?pretty' -H 'Content-Type: application/json' -d'
{
  "mappings": {
    "tweet" : {
      "properties" : {
        "tweet" : {
          "type" :    "text",
          "analyzer": "english"
        },
        "date" : {
          "type" :   "date"
        },
        "name" : {
          "type" :   "text"
        },
        "user_id" : {
          "type" :   "long"
        }
      }
    }
  }
}
'

# keyword 默认是 not_analyzed, text 默认是 analyzed 
curl -XPUT '10.250.140.14:9200/gb/_mapping/tweet?pretty' -H 'Content-Type: application/json' -d'
{
  "properties" : {
    "tag" : {
      "type" :    "text"
    },
    "color" : {
      "type" :    "keyword"
    }
  }
}
'

# 默认是true ,  会创建索引, 没有创建索引不能被查询
curl -XPUT '10.250.140.14:9200/gb/_mapping/tweet?pretty' -H 'Content-Type: application/json' -d'
{
  "properties" : {
    "ageA" : {
      "type" : "integer",
      "index": false
    },
    "ageB" : {
      "type" : "integer"
    }
  }
}
'

curl -XGET '10.250.140.14:9200/gb/_mapping/tweet?pretty'


curl -XGET '10.250.140.14:9200/gb/_analyze?pretty' -H 'Content-Type: application/json' -d'
{
  "field": "tweet",
  "text": "Black-cats" 
}
'
curl -XGET '10.250.140.14:9200/gb/_analyze?pretty' -H 'Content-Type: application/json' -d'
{
  "field": "color",
  "text": "Black-cats" 
}
'

curl -XPUT 'http://10.250.140.14:9200/gb/tweet/14?pretty' -d '
{
   "tweet" : "John Smith",
   "ageA" : 112,
   "ageB" : 11
}
'

# 不能查询，因为未建索引
curl -XGET 'http://10.250.140.14:9200/gb/tweet/_search?pretty' -d '{
    "query" : {
        "match" : {
            "ageA" : 112
        }
    }
}'

# 可以被查询
curl -XGET 'http://10.250.140.14:9200/gb/tweet/_search?pretty' -d '{
    "query" : {
        "match" : {
            "ageB" : 11
        }
    }
}'
```



#### [复杂核心域类型](https://www.elastic.co/guide/cn/elasticsearch/guide/current/complex-core-fields.html)

对于数组，没有特殊的映射需求。任何域都可以包含0、1或者多个值，就像全文域分析得到多个词条。

这暗示 *数组中所有的值必须是相同数据类型的* 。你不能将日期和字符串混在一起。如果你通过索引数组来创建新的域，Elasticsearch 会用数组中第一个值的数据类型作为这个域的 `类型` 。

数组是以多值域 *索引的*—可以搜索，但是无序的。 在搜索的时候，你不能指定 “第一个” 或者 “最后一个”。



Lucene 不理解内部对象。 Lucene 文档是由一组键值对列表组成的。



### [请求体查询](https://www.elastic.co/guide/cn/elasticsearch/guide/current/full-body-search.html)



#### [查询表达式](https://www.elastic.co/guide/cn/elasticsearch/guide/current/query-dsl-intro.html)

```shell
# 空搜索
curl -XGET '10.250.140.14:9200/_search?pretty' -H 'Content-Type: application/json' -d'
{
    "query": {
        "match_all": {}
    }
}'

# 在bool query中minimum_should_match只能紧跟在should的后面，放其他地方会出异常
# "minimum_should_match": 1 表示至少匹配1个
{
    "bool": {
        "must": { "match":   { "email": "business opportunity" }},
        "should": [
            { "match":       { "starred": true }},
            { "bool": {
                "must":      { "match": { "folder": "inbox" }},
                "must_not":  { "match": { "spam": true }}
            }}
        ],
        "minimum_should_match": 1
    }
}

```



#### [最重要的查询](https://www.elastic.co/guide/cn/elasticsearch/guide/current/_most_important_queries.html)

```shell

# match 查询编辑

# 如果你在一个全文字段上使用 match 查询，在执行查询前，它将用正确的分析器去分析查询字符串：

{ "match": { "tweet": "About Search" }}

# 如果在一个精确值的字段上使用它， 例如数字、日期、布尔或者一个 not_analyzed 字符串字段，
# 那么它将会精确匹配给定的值：

{ "match": { "age":    26           }}
{ "match": { "date":   "2014-09-01" }}
{ "match": { "public": true         }}
{ "match": { "tag":    "full_text"  }}

# 对于精确值的查询，你可能需要使用 filter 语句来取代 query，因为 filter 将会被缓存。



# multi_match 查询编辑
#multi_match 查询可以在多个字段上执行相同的 match 查询

{
    "multi_match": {
        "query":    "full text search",
        "fields":   [ "title", "body" ]
    }
}


# range 查询编辑
# range 查询找出那些落在指定区间内的数字或者时间

{
    "range": {
        "age": {
            "gte":  20,
            "lt":   30
        }
    }
}

# gt 大于， gte 大于等于， lt 小于， lte 小于等于



# term 查询
# term 查询被用于精确值 匹配，这些精确值可能是数字、时间、布尔或者那些 not_analyzed 的字符串：

{ "term": { "age":    26           }}
{ "term": { "date":   "2014-09-01" }}
{ "term": { "public": true         }}
{ "term": { "tag":    "full_text"  }}

# term 查询对于输入的文本不 分析 ，所以它将给定的值进行精确查询。

# terms 查询
# terms 查询和 term 查询一样，但它允许你指定多值进行匹配。
# 如果这个字段包含了指定值中的任何一个值，那么这个文档满足条件：

{ "terms": { "tag": [ "search", "full_text", "nosql" ] }}

# 和 term 查询一样，terms 查询对于输入的文本不分析。
# 它查询那些精确匹配的值（包括在大小写、重音、空格等方面的差异）。


# exists 查询和 missing 查询
# exists 查询和 missing 查询被用于查找那些指定字段中有值 (exists) 或无值 (missing) 的文档。
# 这与SQL中的 IS_NULL (missing) 和 NOT IS_NULL (exists) 在本质上具有共性：

{
    "exists":   {
        "field":    "title"
    }
}

# 这些查询经常用于某个字段有值的情况和某个字段缺值的情况。

```





#### [组合多查询](https://www.elastic.co/guide/cn/elasticsearch/guide/current/combining-queries-together.html)

`must`

文档 *必须* 匹配这些条件才能被包含进来。

`must_not`

文档 *必须不* 匹配这些条件才能被包含进来。

`should`

如果满足这些语句中的任意语句，将增加 `_score` ，否则，无任何影响。它们主要用于修正每个文档的相关性得分。

`filter`

*必须* 匹配，但它以不评分、过滤模式来进行。这些语句对评分没有贡献，只是根据过滤标准来排除或包含文档。



```shell
# 查找 title 字段匹配 how to make millions 并且不被标识为 spam 的文档。
# 那些被标识为 starred 或在2014之后的文档，将比另外那些文档拥有更高的排名。
# 如果 _两者_ 都满足，那么它排名将更高
{
    "bool": {
        "must":     { "match": { "title": "how to make millions" }},
        "must_not": { "match": { "tag":   "spam" }},
        "should": [
            { "match": { "tag": "starred" }},
            { "range": { "date": { "gte": "2014-01-01" }}}
        ]
    }
}

#  如果没有 must 语句，那么至少需要能够匹配其中的一条 should 语句。
# 但，如果存在至少一条 must 语句，则对 should 语句的匹配没有要求。


# 增加带过滤器（filtering）的查询编辑
# 如果我们不想因为文档的时间而影响得分，可以用 filter 语句来重写前面的例子：

{
    "bool": {
        "must":     { "match": { "title": "how to make millions" }},
        "must_not": { "match": { "tag":   "spam" }},
        "should": [
            { "match": { "tag": "starred" }}
        ],
        "filter": {
          "range": { "date": { "gte": "2014-01-01" }} 
        }
    }
}


# 将查询移到 bool 查询的 filter 语句中，这样它就自动的转成一个不评分的 filter 了
{
    "bool": {
        "must":     { "match": { "title": "how to make millions" }},
        "must_not": { "match": { "tag":   "spam" }},
        "should": [
            { "match": { "tag": "starred" }}
        ],
        "filter": {
          "bool": { 
              "must": [
                  { "range": { "date": { "gte": "2014-01-01" }}},
                  { "range": { "price": { "lte": 29.99 }}}
              ],
              "must_not": [
                  { "term": { "category": "ebooks" }}
              ]
          }
        }
    }
}


# constant_score 查询编辑
# 它将一个不变的常量评分应用于所有匹配的文档。
# 它被经常用于你只需要执行一个 filter 而没有其它查询（例如，评分查询）的情况下。

# term 查询被放置在 constant_score 中，转成不评分的 filter。
# 这种方式可以用来取代只有 filter 语句的 bool 查询。
{
    "constant_score":   {
        "filter": {
            "term": { "category": "ebooks" } 
        }
    }
}
```







```shell
# 验证查询编辑
curl -XGET '10.250.140.14:9200/gb/tweet/_validate/query?pretty' -H 'Content-Type: application/json' -d'
{
   "query": {
      "tweet" : {
         "match" : "really powerful"
      }
   }
}
'


# 理解错误信息
curl -XGET '10.250.140.14:9200/gb/tweet/_validate/query?explain&pretty' -H 'Content-Type: application/json' -d'
{
   "query": {
      "tweet" : {
         "match" : "really powerful"
      }
   }
}
'



# 理解查询语句
curl -XGET '10.250.140.14:9200/gb,us/_validate/query?explain&pretty' -H 'Content-Type: application/json' -d'
{
   "query": {
      "match" : {
         "tweet" : "really powerful"
      }
   }
}
'

# 不同的分析器结果不同

{
  "valid" : true,
  "_shards" : {
    "total" : 2,
    "successful" : 2,
    "failed" : 0
  },
  "explanations" : [
    {
      "index" : "gb",
      "valid" : true,
      "explanation" : "tweet:realli tweet:power"
    },
    {
      "index" : "us",
      "valid" : true,
      "explanation" : "tweet:really tweet:powerful"
    }
  ]
}

```



### [排序与相关性](https://www.elastic.co/guide/cn/elasticsearch/guide/current/sorting.html)

#### [排序](https://www.elastic.co/guide/cn/elasticsearch/guide/current/_Sorting.html)

```shell
# 使用的是 filter （过滤），没有试图确定这些文档的相关性。 
# 实际上文档将按照随机顺序返回，并且每个文档都会评为零。
curl -XGET '10.250.140.14:9200/_search?pretty' -d '{
    "query" : {
        "bool" : {
            "filter" : {
                "term" : {
                    "user_id" : 1
                }
            }
        }
    }
}'

# 如果评分为零对你造成了困扰，你可以使用 constant_score 查询进行替代
# 这将让所有文档应用一个恒定分数（默认为 1 ）。
curl -XGET '10.250.140.14:9200/_search?pretty' -d '{
    "query" : {
        "constant_score" : {
            "filter" : {
                "term" : {
                    "user_id" : 1
                }
            }
        }
    }
}'



# 对 tweets 进行排序，最新的 tweets 排在最前。 	
# _score 不被计算, 因为它并没有用于排序。
curl -XGET '10.250.140.14:9200/_search?pretty' -H 'Content-Type: application/json' -d'
{
    "query" : {
        "bool" : {
            "filter" : { "term" : { "user_id" : 1 }}
        }
    },
    "sort": { "date": { "order": "desc" }}
}
'

#_score 和 max_score 字段都是 null 。 计算 _score 的花销巨大，通常仅用于排序； 
# 我们并不根据相关性排序，所以记录 _score 是没有意义的。如果无论如何你都要计算 _score ， 
# 你可以将 track_scores 参数设置为 true 。


curl -XGET '10.250.140.14:9200/_search?pretty' -H 'Content-Type: application/json' -d'
{
    "query" : {
        "bool" : {
            "must":   { "match": { "tweet": "manage text search" }},
            "filter" : { "term" : { "user_id" : 2 }}
        }
    },
    "sort": [
        { "date":   { "order": "desc" }},
        { "_score": { "order": "desc" }}
    ]
}
'
# 多级排序并不一定包含 _score 。你可以根据一些不同的字段进行排序， 如地理距离或是脚本计算的特定值。

# Query-string 搜索 也支持自定义排序，可以在查询字符串中使用 sort 参数：
# GET /_search?sort=date:desc&sort=_score&q=search


# 字段多值的排序编辑

# 对于数字或日期，你可以将多值字段减为单值，这可以通过使用 min 、 max 、 avg 或是 sum 排序模式 。 
# 例如你可以按照每个 date 字段中的最早日期进行排序，通过以下方法：
"sort": {
    "dates": {
        "order": "asc",
        "mode":  "min"
    }
}
```



#### [字符串排序与多字段](https://www.elastic.co/guide/cn/elasticsearch/guide/current/multi-fields.html)

```shell
"tweet": { 
    "type":     "string",
    "analyzer": "english",
    "fields": {
        "raw": { 
            "type":  "string",
            "index": "not_analyzed"
        }
    }
}

# tweet 主字段与之前的一样: 是一个 analyzed 全文字段。
# 新的 tweet.raw 子字段是 not_analyzed.


curl -XGET '10.250.140.14:9200/_search?pretty' -H 'Content-Type: application/json' -d'
{
    "query": {
        "match": {
            "tweet": "elasticsearch"
        }
    },
    "sort": "tweet.raw"
}
'

```



#### [什么是相关性?](https://www.elastic.co/guide/cn/elasticsearch/guide/current/relevance-intro.html)

```shell
# JSON格式
curl -XGET '10.250.140.14:9200/_search?explain&pretty' -H 'Content-Type: application/json' -d'
{
   "query"   : { "match" : { "tweet" : "honeymoon" }}
}
'

# YAML格式
curl -XGET '10.250.140.14:9200/_search?explain&pretty&format=yaml' -H 'Content-Type: application/json' -d'
{
   "query"   : { "match" : { "tweet" : "honeymoon" }}
}
'


# 当 explain 选项加到某一文档上时， explain api 会帮助你理解为何这个文档会被匹配，
# 更重要的是，一个文档为何没有被匹配。
curl -XGET '10.250.140.14:9200/us/tweet/12/_explain?pretty' -H 'Content-Type: application/json' -d'
{
   "query" : {
      "bool" : {
         "filter" : { "term" :  { "user_id" : 2           }},
         "must" :  { "match" : { "tweet" :   "honeymoon" }}
      }
   }
}
'

```



### [索引管理](https://www.elastic.co/guide/cn/elasticsearch/guide/current/index-management.html)

#### [索引设置](https://www.elastic.co/guide/cn/elasticsearch/guide/current/index-settings.html)

```shell
curl -XPUT '10.250.140.14:9200/my_temp_index?pretty' -H 'Content-Type: application/json' -d'
{
    "settings": {
        "number_of_shards" :   1,
        "number_of_replicas" : 0
    }
}
'

curl -XPUT '10.250.140.14:9200/my_temp_index/_settings?pretty' -H 'Content-Type: application/json' -d'
{
    "number_of_replicas": 1
}
'


```





#### [配置分析器](https://www.elastic.co/guide/cn/elasticsearch/guide/current/configuring-analyzers.html)

```shell
curl -XPUT '10.250.140.14:9200/spanish_docs?pretty' -H 'Content-Type: application/json' -d'
{
    "settings": {
        "analysis": {
            "analyzer": {
                "es_std": {
                    "type":      "standard",
                    "stopwords": "_spanish_"
                }
            }
        }
    }
}
'

# 10.250.140.14:9200/spanish_docs/_analyze?analyzer=es_std&pretty&text=El veloz zorro marrón
curl -XGET 'http://10.250.140.14:9200/spanish_docs/_analyze?analyzer=es_std&pretty&text=El%20veloz%20zorro%20marr%C3%B3n'





```



#### [自定义分析器](https://www.elastic.co/guide/cn/elasticsearch/guide/current/custom-analyzers.html)

```shell
# 使用 html清除 字符过滤器移除HTML部分。
# 使用一个自定义的 映射 字符过滤器把 & 替换为 " 和 " 。
# 使用 标准 分词器分词。
# 小写词条，使用 小写 词过滤器处理。
# 使用自定义 停止 词过滤器移除自定义的停止词列表中包含的词。
curl -XPUT '10.250.140.14:9200/my_index?pretty' -H 'Content-Type: application/json' -d'
{
    "settings": {
        "analysis": {
            "char_filter": {
                "&_to_and": {
                    "type":       "mapping",
                    "mappings": [ "&=> and "]
            }},
            "filter": {
                "my_stopwords": {
                    "type":       "stop",
                    "stopwords": [ "the", "a" ]
            }},
            "analyzer": {
                "my_analyzer": {
                    "type":         "custom",
                    "char_filter":  [ "html_strip", "&_to_and" ],
                    "tokenizer":    "standard",
                    "filter":       [ "lowercase", "my_stopwords" ]
            }}
}}}
'


curl -XGET '10.250.140.14:9200/my_index2/_analyze?pretty' -H 'Content-Type: application/json' -d'
{
	"analyzer":	"my_analyzer",
  	"text": "The quick & brown fox"
}
'

# 将分析器应用在一个字段上
curl -XPUT '10.250.140.14:9200/my_index/_mapping/my_type?pretty' -H 'Content-Type: application/json' -d'
{
    "properties": {
        "title": {
            "type":      "text",
            "analyzer":  "my_analyzer"
        }
    }
}
'

```





### [分片内部原理](https://www.elastic.co/guide/cn/elasticsearch/guide/current/inside-a-shard.html)

#### [近实时搜索](https://www.elastic.co/guide/cn/elasticsearch/guide/current/near-real-time.html)

```shell
# 关闭自动刷新
curl -XPUT '10.250.140.14:9200/my_logs/_settings?pretty' -H 'Content-Type: application/json' -d '{ "refresh_interval": -1 }'

# 每秒自动刷新
curl -XPUT '10.250.140.14:9200/my_logs/_settings?pretty' -H 'Content-Type: application/json' -d '{ "refresh_interval": "1s" }'
```



## [深入搜索](https://www.elastic.co/guide/cn/elasticsearch/guide/current/search-in-depth.html)

### [结构化搜索](https://www.elastic.co/guide/cn/elasticsearch/guide/current/structured-search.html)



#### [精确值查找](https://www.elastic.co/guide/cn/elasticsearch/guide/current/_finding_exact_values.html)



```shell
curl -XPOST '10.250.140.14:9200/my_store/products/_bulk?pretty' -H 'Content-Type: application/json' -d'
{ "index": { "_id": 1 }}
{ "price" : 10, "productID" : "XHDK-A-1293-#fJ3" }
{ "index": { "_id": 2 }}
{ "price" : 20, "productID" : "KDKE-B-9947-#kL5" }
{ "index": { "_id": 3 }}
{ "price" : 30, "productID" : "JODL-X-1937-#pV7" }
{ "index": { "_id": 4 }}
{ "price" : 30, "productID" : "QQPX-R-3956-#aD8" }
'


# 会使用 constant_score 查询以非评分模式来执行 term 查询并以一作为统一评分
curl -XGET '10.250.140.14:9200/my_store/products/_search?pretty' -H 'Content-Type: application/json' -d'
{
    "query" : {
        "constant_score" : { 
            "filter" : {
                "term" : { 
                    "price" : 20
                }
            }
        }
    }
}
'


# 精确查找
curl -XGET '10.250.140.14:9200/my_store/products/_search?pretty' -H 'Content-Type: application/json' -d'
{
    "query" : {
        "constant_score" : {
            "filter" : {
                "term" : {
                    "productID" : "XHDK-A-1293-#fJ3"
                }
            }
        }
    }
}
'



# 匹配 无打分
curl -XGET '10.250.140.14:9200/my_store/products/_search?pretty' -H 'Content-Type: application/json' -d'
{
    "query" : {
        "constant_score" : {
            "filter" : {
                "match" : {
                    "productID" : "XHDK-A-1293-#fJ3"
                }
            }
        }
    }
}
'

# 匹配 打分
curl -XGET '10.250.140.14:9200/my_store/products/_search?pretty' -H 'Content-Type: application/json' -d'
{
 "query" : {
    "match" : {
    "productID" : "XHDK-A-1293-#fJ3"
    }
  }
}
'

# 
curl -XGET '10.250.140.14:9200/my_store/_analyze?pretty' -H 'Content-Type: application/json' -d'
{
  "field": "productID",
  "text": "XHDK-A-1293-#fJ3"
}
'

```

#### [组合过滤器](https://www.elastic.co/guide/cn/elasticsearch/guide/current/combining-filters.html)

```shell
curl -XGET 'localhost:9200/my_store/products/_search?pretty' -H 'Content-Type: application/json' -d'
{
   "query" : {
      "filtered" : { 
         "filter" : {
            "bool" : {
              "should" : [
                 { "term" : {"price" : 20}}, 
                 { "term" : {"productID" : "XHDK-A-1293-#fJ3"}} 
              ],
              "must_not" : {
                 "term" : {"price" : 30} 
              }
           }
         }
      }
   }
}
'

# 嵌套布尔过滤器
curl -XGET 'localhost:9200/my_store/products/_search?pretty' -H 'Content-Type: application/json' -d'
{
   "query" : {
      "filtered" : {
         "filter" : {
            "bool" : {
              "should" : [
                { "term" : {"productID" : "KDKE-B-9947-#kL5"}}, 
                { "bool" : { 
                  "must" : [
                    { "term" : {"productID" : "JODL-X-1937-#pV7"}}, 
                    { "term" : {"price" : 30}} 
                  ]
                }}
              ]
           }
         }
      }
   }
}
'
```



#### [查找多个精确值](https://www.elastic.co/guide/cn/elasticsearch/guide/current/_finding_multiple_exact_values.html)

```shell
curl -XGET 'localhost:9200/my_store/products/_search?pretty' -H 'Content-Type: application/json' -d'
{
    "query" : {
        "constant_score" : {
            "filter" : {
                "terms" : { 
                    "price" : [20, 30]
                }
            }
        }
    }
}
'
```





#### [范围](https://www.elastic.co/guide/cn/elasticsearch/guide/current/_ranges.html)



```json
# 数值
curl -XGET '10.250.140.14:9200/my_store/products/_search?pretty' -H 'Content-Type: application/json' -d '{
    "query" : {
        "constant_score" : {
            "filter" : {
                "range" : {
                    "price" : {
                        "gte" : 20,
                        "lt"  : 40
                    }
                }
            }
        }
    }
}'


# 日期范围
"range" : {
    "timestamp" : {
        "gt" : "now-1h"
    }
}


"range" : {
    "timestamp" : {
        "gt" : "2014-01-01 00:00:00",
        "lt" : "2014-01-01 00:00:00||+1M" 
    }
}

# 字符串范围
"range" : {
    "title" : {
        "gte" : "a",
        "lt" :  "b"
    }
}
```



#### [处理 Null 值](https://www.elastic.co/guide/cn/elasticsearch/guide/current/_dealing_with_null_values.html)

```shell
curl -XGET 'localhost:9200/my_index/posts/_search?pretty' -H 'Content-Type: application/json' -d'
{
    "query" : {
        "constant_score" : {
            "filter" : {
                "exists" : { "field" : "tags" }
            }
        }
    }
}
'

# 缺失查询
curl -XGET 'localhost:9200/my_index/posts/_search?pretty' -H 'Content-Type: application/json' -d'
{
    "query" : {
        "constant_score" : {
            "filter": {
                "missing" : { "field" : "tags" }
            }
        }
    }
}
'

```

### [全文搜索](https://www.elastic.co/guide/cn/elasticsearch/guide/current/full-text-search.html)



#### [基于词项与基于全文](https://www.elastic.co/guide/cn/elasticsearch/guide/current/term-vs-full-text.html)



#### [匹配查询](https://www.elastic.co/guide/cn/elasticsearch/guide/current/match-query.html)

#### [多词查询](https://www.elastic.co/guide/cn/elasticsearch/guide/current/match-multi-word.html)

```shell
# 提高精度
# match 查询还可以接受 operator 操作符作为输入参数，
# 默认情况下该操作符是 or 。我们可以将它修改成 and 让所有指定词项都必须匹配。
curl -XGET '10.250.140.14:9200/my_index/my_type/_search?pretty' -H 'Content-Type: application/json' -d'
{
    "query": {
        "match": {
            "title": {      
                "query":    "BROWN DOG!",
                "operator": "and"
            }
        }
    }
}
'
# 控制精度
# match 查询支持 minimum_should_match 最小匹配参数， 这让我们可以指定必须匹配的词项数用来
# 表示一个文档是否相关。我们可以将其设置为某个具体数字，更常用的做法是将其设置为一个百分数，
# 因为我们无法控制用户搜索时输入的单词数量
curl -XGET 'localhost:9200/my_index/my_type/_search?pretty' -H 'Content-Type: application/json' -d'
{
  "query": {
    "match": {
      "title": {
        "query":                "quick brown dog",
        "minimum_should_match": "75%"
      }
    }
  }
}
'

# 当给定百分比的时候， minimum_should_match 会做合适的事情：在之前三词项的示例中， 75% 会自动被截断成 # 66.6% ，即三个里面两个词。无论这个值设置成什么，至少包含一个词项的文档才会被认为是匹配的。

```



```shell
{
    "match": {
        "title": {
            "query":                "quick brown fox",
            "minimum_should_match": "75%"
        }
    }
}

<===>

{
  "bool": {
    "should": [
      { "term": { "title": "brown" }},
      { "term": { "title": "fox"   }},
      { "term": { "title": "quick" }}
    ],
    "minimum_should_match": 2 
  }
}
```



#### [查询语句提升权重](https://www.elastic.co/guide/cn/elasticsearch/guide/current/_boosting_query_clauses.html)

```shell

# boost 参数被用来提升一个语句的相对权重（ boost 值大于 1 ）或
# 降低相对权重（ boost 值处于 0 到 1 之间），但是这种提升或降低并不是线性的
curl -XGET 'localhost:9200/_search?pretty' -H 'Content-Type: application/json' -d'
{
    "query": {
        "bool": {
            "must": {
                "match": {  
                    "content": {
                        "query":    "full text search",
                        "operator": "and"
                    }
                }
            },
            "should": [
                { "match": {
                    "content": {
                        "query": "Elasticsearch",
                        "boost": 3 
                    }
                }},
                { "match": {
                    "content": {
                        "query": "Lucene",
                        "boost": 2 
                    }
                }}
            ]
        }
    }
}
'

```





#### [控制分析-analysis](https://www.elastic.co/guide/cn/elasticsearch/guide/current/_controlling_analysis.html)



#### [被破坏的相关度！](https://www.elastic.co/guide/cn/elasticsearch/guide/current/relevance-is-broken.html)

```shell
# dfs 是指 分布式频率搜索
curl -XGET '10.250.140.14:9200/alibaba_alias/employee/_search?pretty&search_type=dfs_query_then_fetch' -H 'Content-Type: application/json' -d'
{
    "query" : {
        "match" : {
            "about" : "rock climbing"
        }
    }
}
'
```





### [多字段搜索](https://www.elastic.co/guide/cn/elasticsearch/guide/current/multi-field-search.html)





#### [多字符串查询](https://www.elastic.co/guide/cn/elasticsearch/guide/current/multi-query-strings.html)

```shell
curl -XGET 'localhost:9200/_search?pretty' -H 'Content-Type: application/json' -d'
{
  "query": {
    "bool": {
      "should": [
        { "match": { "title":  "War and Peace" }},
        { "match": { "author": "Leo Tolstoy"   }},
        { "bool":  {
          "should": [
            { "match": { "translator": "Constance Garnett" }},
            { "match": { "translator": "Louise Maude"      }}
          ]
        }}
      ]
    }
  }
}
'

# 设置语句的权重
curl -XGET 'localhost:9200/_search?pretty' -H 'Content-Type: application/json' -d'
{
  "query": {
    "bool": {
      "should": [
        { "match": { 
            "title":  {
              "query": "War and Peace",
              "boost": 2
        }}},
        { "match": { 
            "author":  {
              "query": "Leo Tolstoy",
              "boost": 2
        }}},
        { "bool":  { 
            "should": [
              { "match": { "translator": "Constance Garnett" }},
              { "match": { "translator": "Louise Maude"      }}
            ]
        }}
      ]
    }
  }
}
'

```



#### [最佳字段](https://www.elastic.co/guide/cn/elasticsearch/guide/current/_best_fields.html)

```shell
# dis_max 查询编辑
{
    "query": {
        "dis_max": {
            "queries": [
                { "match": { "title": "Brown fox" }},
                { "match": { "body":  "Brown fox" }}
            ]
        }
    }
}
```





#### [最佳字段查询调优](https://www.elastic.co/guide/cn/elasticsearch/guide/current/_tuning_best_fields_queries.html)

`tie_breaker` 参数提供了一种 `dis_max` 和 `bool` 之间的折中选择，它的评分方式如下：

1. 获得最佳匹配语句的评分 `_score` 。
2. 将其他匹配语句的评分结果与 `tie_breaker` 相乘。
3. 对以上评分求和并规范化。

```shell
{
    "query": {
        "dis_max": {
            "queries": [
                { "match": { "title": "Quick pets" }},
                { "match": { "body":  "Quick pets" }}
            ],
            "tie_breaker": 0.3
        }
    }
}
```



`tie_breaker` 可以是 `0` 到 `1` 之间的浮点数，其中 `0` 代表使用 `dis_max` 最佳匹配语句的普通逻辑， `1` 表示所有匹配语句同等重要。最佳的精确值需要根据数据与查询调试得出，但是合理值应该与零接近（处于 `0.1 - 0.4` 之间），这样就不会颠覆 `dis_max` 最佳匹配性质的根本。



#### [multi_match 查询](https://www.elastic.co/guide/cn/elasticsearch/guide/current/multi-match-query.html)

```shell
{
  "dis_max": {
    "queries":  [
      {
        "match": {
          "title": {
            "query": "Quick brown fox",
            "minimum_should_match": "30%"
          }
        }
      },
      {
        "match": {
          "body": {
            "query": "Quick brown fox",
            "minimum_should_match": "30%"
          }
        }
      },
    ],
    "tie_breaker": 0.3
  }
}


# 用 multi_match 重写
{
    "multi_match": {
        "query":                "Quick brown fox",
        "type":                 "best_fields", 
        "fields":               [ "title", "body" ],
        "tie_breaker":          0.3,
        "minimum_should_match": "30%" 
    }
}

# 查询字段名称的模糊匹配编辑

{
    "multi_match": {
        "query":  "Quick brown fox",
        "fields": "*_title"
    }
}

# 提升单个字段的权重编辑
# 使用 ^ 字符语法为单个字段提升权重，在字段名称的末尾添加 ^boost ， 其中 boost 是一个浮点数
{
    "multi_match": {
        "query":  "Quick brown fox",
        "fields": [ "*_title", "chapter_title^2" ] 
    }
}

```





#### [多数字段](https://www.elastic.co/guide/cn/elasticsearch/guide/current/most-fields.html)

相同的文本索引到其他字段从而提供更为精确的匹配。一个字段可能是为词干未提取过的版本，另一个字段可能是变音过的原始词，第三个可能使用 *shingles* 提供 [词语相似性](https://www.elastic.co/guide/cn/elasticsearch/guide/current/proximity-matching.html) 信息。这些附加的字段可以看成提高每个文档的相关度评分的信号 *signals* ，能匹配字段的越多越好。

```shell
# 多字段映射
# 对我们的字段索引两次： 一次使用词干模式以及一次非词干模式。
curl -XDELETE '10.250.140.14:9200/my_index?pretty'
curl -XPUT '10.250.140.14:9200/my_index?pretty' -H 'Content-Type: application/json' -d'
{
    "settings": { "number_of_shards": 1 }, 
    "mappings": {
        "my_type": {
            "properties": {
                "title": { 
                    "type":     "string",
                    "analyzer": "english",
                    "fields": {
                        "std":   { 
                            "type":     "string",
                            "analyzer": "standard"
                        }
                    }
                }
            }
        }
    }
}
'

curl -XPUT '10.250.140.14:9200/my_index/my_type/1?pretty' -H 'Content-Type: application/json' -d'
{ "title": "My rabbit jumps" }
'
curl -XPUT '10.250.140.14:9200/my_index/my_type/2?pretty' -H 'Content-Type: application/json' -d'
{ "title": "Jumping jack rabbits" }
'



curl -XGET '10.250.140.14:9200/my_index/_search?pretty' -H 'Content-Type: application/json' -d'
{
   "query": {
        "match": {
            "title": "jumping rabbits"
        }
    }
}
'

# 如果只是查询 title.std 字段，那么只有文档 2 是匹配的。
curl -XGET '10.250.140.14:9200/my_index/_search?pretty' -H 'Content-Type: application/json' -d'
{
   "query": {
        "match": {
            "title.std": "jumping rabbits"
        }
    }
}
'

curl -XGET '10.250.140.14:9200/my_index/_search?pretty' -H 'Content-Type: application/json' -d'
{
   "query": {
        "multi_match": {
            "query":  "jumping rabbits",
            "type":   "most_fields", 
            "fields": [ "title", "title.std" ]
        }
    }
}'

# 用广度匹配字段 title 包括尽可能多的文档——以提升召回率——同时又使用字段 
# title.std 作为 信号 将相关度更高的文档置于结果顶部。

# 每个字段对于最终评分的贡献可以通过自定义值 boost 来控制。
# 比如，使 title 字段更为重要，这样同时也降低了其他信号字段的作用
curl -XGET '10.250.140.14:9200/my_index/_search?pretty' -H 'Content-Type: application/json' -d'
{
   "query": {
        "multi_match": {
            "query":       "jumping rabbits",
            "type":        "most_fields",
            "fields":      [ "title^10", "title.std" ] 
        }
    }
}
'


```



#### [跨字段实体搜索](https://www.elastic.co/guide/cn/elasticsearch/guide/current/_cross_fields_entity_search.html)

```
{
  "query": {
    "bool": {
      "should": [
        { "match": { "street":    "Poland Street W1V" }},
        { "match": { "city":      "Poland Street W1V" }},
        { "match": { "country":   "Poland Street W1V" }},
        { "match": { "postcode":  "Poland Street W1V" }}
      ]
    }
  }
}


# 采用 multi_match 查询， 将 type 设置成 most_fields 然后告诉 Elasticsearch 合并所有匹配字段的评分
{
  "query": {
    "multi_match": {
      "query":       "Poland Street W1V",
      "type":        "most_fields",
      "fields":      [ "street", "city", "country", "postcode" ]
    }
  }
}

```

**most_fields 方式的问题**

用 most_fields 这种方式搜索也存在某些问题，这些问题并不会马上显现：

它是为多数字段匹配 任意 词设计的，而不是在 所有字段 中找到最匹配的。
它不能使用 operator 或 minimum_should_match 参数来降低次相关结果造成的长尾效应。
词频对于每个字段是不一样的，而且它们之间的相互影响会导致不好的排序结果。



#### [字段中心式查询](https://www.elastic.co/guide/cn/elasticsearch/guide/current/field-centric.html)



#### [自定义 _all 字段](https://www.elastic.co/guide/cn/elasticsearch/guide/current/custom-all.html)

```shell
curl -XPUT '10.250.140.14:9200/my_index20?pretty' -H 'Content-Type: application/json' -d'
{
    "mappings": {
        "person": {
            "properties": {
                "first_name": {
                    "type":     "string",
                    "copy_to":  "full_name" 
                },
                "last_name": {
                    "type":     "string",
                    "copy_to":  "full_name" 
                },
                "full_name": {
                    "type":     "string"
                }
            }
        }
    }
}
'



curl -XPUT '10.250.140.14:9200/my_index22?pretty' -H 'Content-Type: application/json' -d'
{
    "mappings": {
        "person": {
            "properties": {
                "first_name": {
                    "type" : "text",
                    "copy_to": "full_name", 
                    "fields": {
                        "raw": {
                            "type" : "keyword"
                        }
                    }
                },
                "full_name": {
                    "type":     "string"
                }
            }
        }
    }
}'

```



#### [cross-fields 跨字段查询](https://www.elastic.co/guide/cn/elasticsearch/guide/current/_cross_fields_queries.html)

```shell
# 字段中心式（field-centric）与词中心式（term-centric）这两种查询方式的不同
# 字段中心式
curl -XGET '10.250.140.14:9200/gb/_validate/query?explain&pretty' -H 'Content-Type: application/json' -d'
{
    "query": {
        "multi_match": {
            "query":       "peter smith",
            "type":        "most_fields",
            "operator":    "and", 
            "fields":      [ "first_name", "last_name" ]
        }
    }
}
'

# 词中心式
curl -XGET '10.250.140.14:9200/gb/_validate/query?explain&pretty' -H 'Content-Type: application/json' -d'
{
    "query": {
        "multi_match": {
            "query":       "peter smith",
            "type":        "cross_fields", 
            "operator":    "and",
            "fields":      [ "first_name", "last_name" ]
        }
    }
}
'

```

**按字段提高权重**

```shell
# 要用 title 和 description 字段搜索图书，可能希望为 title 分配更多的权重
GET /books/_search
{
    "query": {
        "multi_match": {
            "query":       "peter smith",
            "type":        "cross_fields",
            "fields":      [ "title^2", "description" ] 
        }
    }
}
```



自定义单字段查询是否能够优于多字段查询，取决于在多字段查询与单字段自定义 `_all` 之间代价的权衡，即哪种解决方案会带来更大的性能优化就选择哪一种。





#### [Exact-Value 精确值字段](https://www.elastic.co/guide/cn/elasticsearch/guide/current/_exact_value_fields.html)



###  [近似匹配](https://www.elastic.co/guide/cn/elasticsearch/guide/current/proximity-matching.html)



#### [短语匹配](https://www.elastic.co/guide/cn/elasticsearch/guide/current/phrase-matching.html)

```shell
curl -XGET '10.250.140.14:9200/my_index/my_type/_search?pretty' -H 'Content-Type: application/json' -d'
{
    "query": {
        "match_phrase": {
            "title": "quick brown fox"
        }
    }
}
'

# match_phrase 查询同样可写成一种类型为 phrase 的 match 查询

"match": {
    "title": {
        "query": "quick brown fox",
        "type":  "phrase"
    }
}
```

​	

```shell
curl -XGET 'http://10.250.140.14:9200/_analyze?analyzer=standard&text=Quick%20brown%20fox&pretty'
```





#### [混合起来](https://www.elastic.co/guide/cn/elasticsearch/guide/current/slop.html)

```shell
curl -XGET '10.250.140.14:9200/my_index/my_type/_search?pretty' -H 'Content-Type: application/json' -d'
{
    "query": {
        "match_phrase": {
            "title": {
                "query": "quick fox",
                "slop":  1
            }
        }
    }
}
'

```









```shell


curl -XDELETE '10.250.140.14:9200/my_index/groups/?pretty'
curl -XPUT '10.250.140.14:9200/my_index/_mapping/groups?pretty' -H 'Content-Type: application/json' -d'
{
    "properties": {
        "names": {
            "type":                "string",
            "position_increment_gap": 100
        }
    }
}
'

curl -XPUT '10.250.140.14:9200/my_index/groups/1?pretty' -H 'Content-Type: application/json' -d'
{
    "names": [ "John Abraham", "Lincoln Smith"]
}
'

curl -XGET '10.250.140.14:9200/my_index/groups/_search?pretty' -H 'Content-Type: application/json' -d'
{
    "query": {
        "match_phrase": {
            "names": "Abraham Lincoln"
        }
    }
}
'
```





#### [越近越好](https://www.elastic.co/guide/cn/elasticsearch/guide/current/_closer_is_better.html)

```
curl -XPOST 'localhost:9200/my_index/my_type/_search?pretty' -H 'Content-Type: application/json' -d'
{
   "query": {
      "match_phrase": {
         "title": {
            "query": "quick dog",
            "slop":  50 
         }
      }
   }
}
'

```



#### [使用邻近度提高相关度](https://www.elastic.co/guide/cn/elasticsearch/guide/current/proximity-relevance.html)

```
curl -XGET 'localhost:9200/my_index/my_type/_search?pretty' -H 'Content-Type: application/json' -d'
{
  "query": {
    "bool": {
      "must": {
        "match": { 
          "title": {
            "query":                "quick brown fox",
            "minimum_should_match": "30%"
          }
        }
      },
      "should": {
        "match_phrase": { 
          "title": {
            "query": "quick brown fox",
            "slop":  50
          }
        }
      }
    }
  }
}
'

```



#### [性能优化](https://www.elastic.co/guide/cn/elasticsearch/guide/current/_Improving_Performance.html)

```shell
curl -XGET 'localhost:9200/my_index/my_type/_search?pretty' -H 'Content-Type: application/json' -d'
{
    "query": {
        "match": {  
            "title": {
                "query":                "quick brown fox",
                "minimum_should_match": "30%"
            }
        }
    },
    "rescore": {
        "window_size": 50, 
        "query": {         
            "rescore_query": {
                "match_phrase": {
                    "title": {
                        "query": "quick brown fox",
                        "slop":  50
                    }
                }
            }
        }
    }
}
'
```



#### [寻找相关词](https://www.elastic.co/guide/cn/elasticsearch/guide/current/shingles.html)

```shell
# 创建分析器时使用 shingle 语汇单元过滤器
curl -XDELETE '10.250.140.14:9200/my_index?pretty'
curl -XPUT '10.250.140.14:9200/my_index?pretty' -H 'Content-Type: application/json' -d'
{
    "settings": {
        "number_of_shards": 1,  
        "analysis": {
            "filter": {
                "my_shingle_filter": {
                    "type":             "shingle",
                    "min_shingle_size": 2, 
                    "max_shingle_size": 2, 
                    "output_unigrams":  false   
                }
            },
            "analyzer": {
                "my_shingle_analyzer": {
                    "type":             "custom",
                    "tokenizer":        "standard",
                    "filter": [
                        "lowercase",
                        "my_shingle_filter" 
                    ]
                }
            }
        }
    }
}
'



curl -XGET 'http://10.250.140.14:9200/my_index555/_analyze?analyzer=my_shingle_analyzer&text=Sue%20ate%20the%20alligator&pretty'



# 多字段
curl -XDELETE '10.250.140.14:9200/my_index?pretty'
curl -XPUT '10.250.140.14:9200/my_index/_mapping/my_type?pretty' -H 'Content-Type: application/json' -d'
{
    "my_type": {
        "properties": {
            "title": {
                "type": "string",
                "fields": {
                    "shingles": {
                        "type":     "string",
                        "analyzer": "my_shingle_analyzer"
                    }
                }
            }
        }
    }
}'


curl -XPOST '10.250.140.14:9200/my_index/my_type/_bulk?pretty' -H 'Content-Type: application/json' -d '
{ "index": { "_id": 1 }}
{ "title": "Sue ate the alligator" }
{ "index": { "_id": 2 }}
{ "title": "The alligator ate Sue" }
{ "index": { "_id": 3 }}
{ "title": "Sue never goes anywhere without her alligator skin purse" }
'


curl -XGET 'http://10.250.140.14:9200/my_index/my_type/_search?pretty' -d '
{
   "query": {
        "match": {
           "title": "the hungry alligator ate sue"
        }
   }
}'


curl -XGET 'http://10.250.140.14:9200/my_index/my_type/_search?pretty' -d '{
   "query": {
      "bool": {
         "must": {
            "match": {
               "title": "the hungry alligator ate sue"
            }
         },
         "should": {
            "match": {
               "title.shingles": "the hungry alligator ate sue"
            }
         }
      }
   }
}'
```





### [部分匹配](https://www.elastic.co/guide/cn/elasticsearch/guide/current/partial-matching.html)



#### [邮编与结构化数据](https://www.elastic.co/guide/cn/elasticsearch/guide/current/_postcodes_and_structured_data.html)



```shell
curl -XDELETE '10.250.140.14:9200/my_index?pretty'
curl -XPUT '10.250.140.14:9200/my_index?pretty' -H 'Content-Type: application/json' -d'
{
    "mappings": {
        "address": {
            "properties": {
                "postcode": {
                    "type":  "string",
                    "index": "not_analyzed"
                }
            }
        }
    }
}
'

curl -XPUT '10.250.140.14:9200/my_index/address/1?pretty' -H 'Content-Type: application/json' -d'
{ "postcode": "W1V 3DG" }
'
curl -XPUT '10.250.140.14:9200/my_index/address/2?pretty' -H 'Content-Type: application/json' -d'
{ "postcode": "W2F 8HW" }
'
curl -XPUT '10.250.140.14:9200/my_index/address/3?pretty' -H 'Content-Type: application/json' -d'
{ "postcode": "W1F 7HW" }
'
curl -XPUT '10.250.140.14:9200/my_index/address/4?pretty' -H 'Content-Type: application/json' -d'
{ "postcode": "WC1N 1LZ" }
'
curl -XPUT '10.250.140.14:9200/my_index/address/5?pretty' -H 'Content-Type: application/json' -d'
{ "postcode": "SW5 0BE" }
'


```



#### [prefix 前缀查询](https://www.elastic.co/guide/cn/elasticsearch/guide/current/prefix-query.html)

```shell
curl -XGET '10.250.140.14:9200/my_index/address/_search?pretty' -H 'Content-Type: application/json' -d'
{
    "query": {
        "prefix": {
            "postcode": "W1"
        }
    }
}
'
```



#### [通配符与正则表达式查询](https://www.elastic.co/guide/cn/elasticsearch/guide/current/_wildcard_and_regexp_queries.html)

```shell
# 使用标准的 shell 通配符查询： ? 匹配任意字符， * 匹配 0 或多个字符。

curl -XGET '10.250.140.14:9200/my_index/address/_search?pretty' -H 'Content-Type: application/json' -d'
{
    "query": {
        "wildcard": {
            "postcode": "W?F*HW" 
        }
    }
}
'

# 想匹配只以 W 开始并跟随一个数字的所有邮编
curl -XGET '10.250.140.14:9200/my_index/address/_search?pretty' -H 'Content-Type: application/json' -d'
{
    "query": {
        "regexp": {
            "postcode": "W[0-9].+" 
        }
    }
}
'

# prefix 、 wildcard 和 regexp 查询是基于词操作的，如果用它们来查询 analyzed 字段，
# 它们会检查字段里面的每个词，而不是将字段作为整体来处理。

```





#### [查询时输入即搜索](https://www.elastic.co/guide/cn/elasticsearch/guide/current/_query_time_search_as_you_type.html)

```shell
{
    "match_phrase_prefix" : {
        "brand" : "johnnie walker bl"
    }
}


# 接受 slop 参数让相对词序位置不那么严格
{
    "match_phrase_prefix" : {
        "brand" : {
            "query": "walker johnnie bl", 
            "slop":  10
        }
    }
}


# 参数 max_expansions 控制着可以与前缀匹配的词的数量
{
    "match_phrase_prefix" : {
        "brand" : {
            "query":          "johnnie walker bl",
            "max_expansions": 50
        }
    }
}



```



#### [索引时优化](https://www.elastic.co/guide/cn/elasticsearch/guide/current/_index_time_optimizations.html)



#### [Ngrams 在部分匹配的应用](https://www.elastic.co/guide/cn/elasticsearch/guide/current/_ngrams_for_partial_matching.html)



#### [索引时输入即搜索](https://www.elastic.co/guide/cn/elasticsearch/guide/current/_index_time_search_as_you_type.html)

```shell

curl -XDELETE '10.250.140.14:9200/my_index111?pretty'
curl -XPUT '10.250.140.14:9200/my_index111?pretty' -H 'Content-Type: application/json' -d'
{
    "settings": {
        "number_of_shards": 1, 
        "analysis": {
            "filter": {
                "autocomplete_filter": { 
                    "type":     "edge_ngram",
                    "min_gram": 1,
                    "max_gram": 20
                }
            },
            "analyzer": {
                "autocomplete": {
                    "type":      "custom",
                    "tokenizer": "standard",
                    "filter": [
                        "lowercase",
                        "autocomplete_filter" 
                    ]
                }
            }
        }
    }
}
'



curl -XGET '10.250.140.14:9200/my_index111/_analyze?pretty' -H 'Content-Type: application/json' -d '{
  "analyzer": "autocomplete",
  "text": "quick brown"
}'





-------
curl -XPUT '10.250.140.14:9200/my_index111/_mapping/my_type?pretty' -H 'Content-Type: application/json' -d'
{
    "my_type": {
        "properties": {
            "name": {
                "type":     "string",
                "analyzer": "autocomplete"
            }
        }
    }
}
'


curl -XPOST '10.250.140.14:9200/my_index111/my_type/_bulk?pretty' -H 'Content-Type: application/json' -d'
{ "index": { "_id": 1            }}
{ "name": "Brown foxes"    }
{ "index": { "_id": 2            }}
{ "name": "Yellow furballs" }
'

curl -XGET '10.250.140.14:9200/my_index111/_mapping?pretty'

curl -XGET '10.250.140.14:9200/my_index111/my_type/_search?pretty' -H 'Content-Type: application/json' -d'
{
    "query": {
        "match": {
            "name": "brown fo"
        }
    }
}
'

curl -XGET '10.250.140.14:9200/my_index111/my_type/_validate/query?explain&pretty' -H 'Content-Type: application/json' -d'
{
    "query": {
        "match": {
            "name": "brown fo"
        }
    }
}
'

# 我们需要保证倒排索引表中包含边界 n-grams 的每个词，
# 但是我们只想匹配用户输入的完整词组（ brown 和 fo ）， 
# 可以通过在索引时使用 autocomplete 分析器，并在搜索时使用 standard 标准分析器来实现这种想法
curl -XGET '10.250.140.14:9200/my_index111/my_type/_search?pretty' -H 'Content-Type: application/json' -d'
{
    "query": {
        "match": {
            "name": {
                "query":    "brown fo",
                "analyzer": "standard" 
            }
        }
    }
}
'


curl -XPUT '10.250.140.14:9200/my_index111/my_type/_mapping?pretty' -H 'Content-Type: application/json' -d'
{
	"my_type": {
		"properties": {
			"name": {
				"type": "text",
				"analyzer": "autocomplete",
				"search_analyzer": "standard"
			}
		}
	}
}
'

curl -XGET '10.250.140.14:9200/my_index111/my_type/_validate/query?explain&pretty' -H 'Content-Type: application/json' -d'
{
    "query": {
        "match": {
            "name": "brown fo"
        }
    }
}
'


# postcode_index 分析器使用 postcode_filter 将邮编转换成边界 n-gram 形式。
# postcode_search 分析器可以将搜索词看成 not_analyzed 未分析的。
{
    "analysis": {
        "filter": {
            "postcode_filter": {
                "type":     "edge_ngram",
                "min_gram": 1,
                "max_gram": 8
            }
        },
        "analyzer": {
            "postcode_index": { 
                "tokenizer": "keyword",
                "filter":    [ "postcode_filter" ]
            },
            "postcode_search": { 
                "tokenizer": "keyword"
            }
        }
    }
}
```



#### [Ngrams 在复合词的应用](https://www.elastic.co/guide/cn/elasticsearch/guide/current/ngrams-compound-words.html)

```shell
curl -XDELETE '10.250.140.14:9200/my_index?pretty'
curl -XPUT '10.250.140.14:9200/my_index?pretty' -H 'Content-Type: application/json' -d'
{
    "settings": {
        "analysis": {
            "filter": {
                "trigrams_filter": {
                    "type":     "ngram",
                    "min_gram": 3,
                    "max_gram": 3
                }
            },
            "analyzer": {
                "trigrams": {
                    "type":      "custom",
                    "tokenizer": "standard",
                    "filter":   [
                        "lowercase",
                        "trigrams_filter"
                    ]
                }
            }
        }
    },
    "mappings": {
        "my_type": {
            "properties": {
                "text": {
                    "type":     "string",
                    "analyzer": "trigrams" 
                }
            }
        }
    }
}
'



curl -XGET '10.250.140.14:9200/my_index/_analyze?pretty' -H 'Content-Type: application/json' -d '{
  "analyzer": "trigrams",
  "text": "Weißkopfseeadler"
}'


curl -XPOST '10.250.140.14:9200/my_index/my_type/_bulk?pretty' -H 'Content-Type: application/json' -d'
{ "index": { "_id": 1 }}
{ "text": "Aussprachewörterbuch" }
{ "index": { "_id": 2 }}
{ "text": "Militärgeschichte" }
{ "index": { "_id": 3 }}
{ "text": "Weißkopfseeadler" }
{ "index": { "_id": 4 }}
{ "text": "Weltgesundheitsorganisation" }
{ "index": { "_id": 5 }}
{ "text": "Rindfleischetikettierungsüberwachungsaufgabenübertragungsgesetz" }
'


curl -XGET '10.250.140.14:9200/my_index/my_type/_search?pretty' -H 'Content-Type: application/json' -d'
{
    "query": {
        "match": {
            "text": "Adler"
        }
    }
}
'



curl -XGET '10.250.140.14:9200/my_index/my_type/_search?pretty' -H 'Content-Type: application/json' -d'
{
    "query": {
        "match": {
            "text": {
                "query":                "Gesundheit",
                "minimum_should_match": "80%"
            }
        }
    }
}
'
```



### [控制相关度](https://www.elastic.co/guide/cn/elasticsearch/guide/current/controlling-relevance.html)



#### [相关度评分背后的理论](https://www.elastic.co/guide/cn/elasticsearch/guide/current/scoring-theory.html)

```shell
# 将参数 index_options 设置为 docs 可以禁用词频统计及词频位置，这个映射的字段不会计算词的出现次数，
# 对于短语或近似查询也不可用。要求精确查询的 not_analyzed 字符串字段会默认使用该设置。
PUT /my_index
{
  "mappings": {
    "doc": {
      "properties": {
        "text": {
          "type":          "string",
          "index_options": "docs" 
        }
      }
    }
  }
}


# 这个字段不会将字段长度归一值考虑在内，长字段和短字段会以相同长度计算评分。
PUT /my_index
{
  "mappings": {
    "doc": {
      "properties": {
        "text": {
          "type": "string",
          "norms": { "enabled": false } 
        }
      }
    }
  }
}
```





#### [Lucene 的实用评分函数](https://www.elastic.co/guide/cn/elasticsearch/guide/current/practical-scoring-function.html)





#### [查询时权重提升](https://www.elastic.co/guide/cn/elasticsearch/guide/current/query-time-boosting.html)

```shell
GET /_search
{
  "query": {
    "bool": {
      "should": [
        {
          "match": {
            "title": {
              "query": "quick brown fox",
              "boost": 2 
            }
          }
        },
        {
          "match": { 
            "content": "quick brown fox"
          }
        }
      ]
    }
  }
}

# 提升索引权重
GET /docs_2014_*/_search 
{
  "indices_boost": { 
    "docs_2014_10": 3,
    "docs_2014_09": 2
  },
  "query": {
    "match": {
      "text": "quick brown fox"
    }
  }
}
```



#### [使用查询结构修改相关度](https://www.elastic.co/guide/cn/elasticsearch/guide/current/query-scoring.html)





#### [Not Quite Not](https://www.elastic.co/guide/cn/elasticsearch/guide/current/not-quite-not.html)

```shell
GET /_search
{
  "query": {
    "bool": {
      "must": {
        "match": {
          "text": "apple"
        }
      },
      "must_not": {
        "match": {
          "text": "pie tart fruit crumble tree"
        }
      }
    }
  }
}

# 权重提升查询编辑
GET /_search
{
  "query": {
    "boosting": {
      "positive": {
        "match": {
          "text": "apple"
        }
      },
      "negative": {
        "match": {
          "text": "pie tart fruit crumble tree"
        }
      },
      "negative_boost": 0.5
    }
  }
}
```



#### [忽略 TF/IDF](https://www.elastic.co/guide/cn/elasticsearch/guide/current/ignoring-tfidf.html)







```shell
GET /_search
{
  "query": {
    "bool": {
      "should": [
        { "constant_score": {
          "query": { "match": { "description": "wifi" }}
        }},
        { "constant_score": {
          "query": { "match": { "description": "garden" }}
        }},
        { "constant_score": {
          "query": { "match": { "description": "pool" }}
        }}
      ]
    }
  }
}



GET /_search
{
  "query": {
    "bool": {
      "should": [
        { "constant_score": {
          "query": { "match": { "description": "wifi" }}
        }},
        { "constant_score": {
          "query": { "match": { "description": "garden" }}
        }},
        { "constant_score": {
          "boost":   2 
          "query": { "match": { "description": "pool" }}
        }}
      ]
    }
  }
}



GET /_search
{
  "query": {
    "bool": {
      "should": [
        { "constant_score": {
          "query": { "match": { "features": "wifi" }}
        }},
        { "constant_score": {
          "query": { "match": { "features": "garden" }}
        }},
        { "constant_score": {
          "boost":   2
          "query": { "match": { "features": "pool" }}
        }}
      ]
    }
  }
}
```



#### [function_score 查询](https://www.elastic.co/guide/cn/elasticsearch/guide/current/function-score-query.html)





#### [按受欢迎度提升权重](https://www.elastic.co/guide/cn/elasticsearch/guide/current/boosting-by-popularity.html)



```shell
PUT /blogposts/post/1
{
  "title":   "About popularity",
  "content": "In this post we will talk about...",
  "votes":   6
}

GET /blogposts/post/_search
{
  "query": {
    "function_score": { 
      "query": { 
        "multi_match": {
          "query":    "popularity",
          "fields": [ "title", "content" ]
        }
      },
      "field_value_factor": { 
        "field": "votes" 
      }
    }
  }
}


GET /blogposts/post/_search
{
  "query": {
    "function_score": {
      "query": {
        "multi_match": {
          "query":    "popularity",
          "fields": [ "title", "content" ]
        }
      },
      "field_value_factor": {
        "field":    "votes",
        "modifier": "log1p" 
      }
    }
  }
}

# factor
GET /blogposts/post/_search
{
  "query": {
    "function_score": {
      "query": {
        "multi_match": {
          "query":    "popularity",
          "fields": [ "title", "content" ]
        }
      },
      "field_value_factor": {
        "field":    "votes",
        "modifier": "log1p",
        "factor":   2 
      }
    }
  }
}

# boost_mode
GET /blogposts/post/_search
{
  "query": {
    "function_score": {
      "query": {
        "multi_match": {
          "query":    "popularity",
          "fields": [ "title", "content" ]
        }
      },
      "field_value_factor": {
        "field":    "votes",
        "modifier": "log1p",
        "factor":   0.1
      },
      "boost_mode": "sum" 
    }
  }
}


# max_boost
GET /blogposts/post/_search
{
  "query": {
    "function_score": {
      "query": {
        "multi_match": {
          "query":    "popularity",
          "fields": [ "title", "content" ]
        }
      },
      "field_value_factor": {
        "field":    "votes",
        "modifier": "log1p",
        "factor":   0.1
      },
      "boost_mode": "sum",
      "max_boost":  1.5 
    }
  }
}
```







#### [过滤集提升权重](https://www.elastic.co/guide/cn/elasticsearch/guide/current/function-score-filters.html)

```shell
GET /_search
{
  "query": {
    "function_score": {
      "filter": { 
        "term": { "city": "Barcelona" }
      },
      "functions": [ 
        {
          "filter": { "term": { "features": "wifi" }}, 
          "weight": 1
        },
        {
          "filter": { "term": { "features": "garden" }}, 
          "weight": 1
        },
        {
          "filter": { "term": { "features": "pool" }}, 
          "weight": 2 
        }
      ],
      "score_mode": "sum", 
    }
  }
}
```



#### [随机评分](https://www.elastic.co/guide/cn/elasticsearch/guide/current/random-scoring.html)

```shell
GET /_search
{
  "query": {
    "function_score": {
      "filter": {
        "term": { "city": "Barcelona" }
      },
      "functions": [
        {
          "filter": { "term": { "features": "wifi" }},
          "weight": 1
        },
        {
          "filter": { "term": { "features": "garden" }},
          "weight": 1
        },
        {
          "filter": { "term": { "features": "pool" }},
          "weight": 2
        },
        {
          "random_score": { 
            "seed":  "the users session id" 
          }
        }
      ],
      "score_mode": "sum"
    }
  }
}
```





#### [越近越好](https://www.elastic.co/guide/cn/elasticsearch/guide/current/decay-functions.html)

```shell
GET /_search
{
  "query": {
    "function_score": {
      "functions": [
        {
          "gauss": {
            "location": { 
              "origin": { "lat": 51.5, "lon": 0.12 },
              "offset": "2km",
              "scale":  "3km"
            }
          }
        },
        {
          "gauss": {
            "price": { 
              "origin": "50", 
              "offset": "50",
              "scale":  "20"
            }
          },
          "weight": 2 
        }
      ]
    }
  }
}
```



#### [理解 price 价格语句](https://www.elastic.co/guide/cn/elasticsearch/guide/current/Understanding-the-price-Clause.html)





#### [脚本评分](https://www.elastic.co/guide/cn/elasticsearch/guide/current/script-score.html)





#### [可插拔的相似度算法](https://www.elastic.co/guide/cn/elasticsearch/guide/current/pluggable-similarites.html)



#### [更改相似度](https://www.elastic.co/guide/cn/elasticsearch/guide/current/changing-similarities.html)

```shell
PUT /my_index
{
  "mappings": {
    "doc": {
      "properties": {
        "title": {
          "type":       "string",
          "similarity": "BM25" 
        },
        "body": {
          "type":       "string",
          "similarity": "default" 
        }
      }
  }
}




PUT /my_index
{
  "settings": {
    "similarity": {
      "my_bm25": { 
        "type": "BM25",
        "b":    0 
      }
    }
  },
  "mappings": {
    "doc": {
      "properties": {
        "title": {
          "type":       "string",
          "similarity": "my_bm25" 
        },
        "body": {
          "type":       "string",
          "similarity": "BM25" 
        }
      }
    }
  }
}
```





#### [调试相关度是最后 10% 要做的事情](https://www.elastic.co/guide/cn/elasticsearch/guide/current/relevance-conclusion.html)



## [处理人类语言](https://www.elastic.co/guide/cn/elasticsearch/guide/current/languages.html)



### [开始处理各种语言](https://www.elastic.co/guide/cn/elasticsearch/guide/current/language-intro.html)



#### [使用语言分析器](https://www.elastic.co/guide/cn/elasticsearch/guide/current/using-language-analyzers.html)

```shell

curl -XDELETE '10.250.140.14:9200/my_index?pretty'
curl -XPUT '10.250.140.14:9200/my_index?pretty' -H 'Content-Type: application/json' -d'
{
  "mappings": {
    "blog": {
      "properties": {
        "title": {
          "type":     "string",
          "analyzer": "english" 
        }
      }
    }
  }
}'



curl -XDELETE '10.250.140.14:9200/my_index?pretty'
curl -XPUT '10.250.140.14:9200/my_index?pretty' -H 'Content-Type: application/json' -d'
{
  "mappings": {
    "blog": {
      "properties": {
        "title": { 
          "type": "text",
          "fields": {
            "english": { 
              "type":     "text",
              "analyzer": "english"
            },
            "keyword": {
              "ignore_above": 256,
              "type": "keyword"
            }
          }
        }
      }
    }
  }
}'



curl -XPUT '10.250.140.14:9200/my_index/blog/1?pretty'  -d '{
	"title": "Im happy for this fox"
}'


curl -XPUT '10.250.140.14:9200/my_index/blog/2?pretty' -d '{
	"title": "Im not happy about my fox problem"
}'


curl -XGET '10.250.140.14:9200/my_index/_search?pretty' -H 'Content-Type: application/json' -d'
{
  "query": {
    "multi_match": {
      "type":     "most_fields", 
      "query":    "not happy foxes",
      "fields": [ "title", "title.english","title.keyword" ]
    }
  }
}'

curl -XGET '10.250.140.14:9200/my_index/_mapping?pretty'

curl -XGET '10.250.140.14:9200/my_index/_search?pretty' -H 'Content-Type: application/json' -d'
{
    "query" : {
        "constant_score" : {
            "filter" : {
                "term" : {
                    "title.keyword" : "Im happy for this fox"
                }
            }
        }
    }
}
'
```



#### [配置语言分析器](https://www.elastic.co/guide/cn/elasticsearch/guide/current/configuring-language-analyzers.html)

#### [混合语言的陷阱](https://www.elastic.co/guide/cn/elasticsearch/guide/current/language-pitfalls.html)





```shell
PUT /blogs-en
{
	"mappings": {
		"post": {
			"properties": {
				"title": {
					"type": "string",
					"fields": {
						"stemmed": {
							"type": "string",
							"analyzer": "english"
						}
					}
				}
			}
		}
	}
}

PUT /blogs-fr
{
	"mappings": {
		"post": {
			"properties": {
				"title": {
					"type": "string",
					"fields": {
						"stemmed": {
							"type": "string",
							"analyzer": "french"
						}
					}
				}
			}
		}
	}
}



GET /blogs-*/post/_search 
{
    "query": {
        "multi_match": {
            "query":   "deja vu",
            "fields":  [ "title", "title.stemmed" ] 
            "type":    "most_fields"
        }
    },
    "indices_boost": { 
        "blogs-en": 3,
        "blogs-fr": 2
    }
}
```



#### [每个域一种语言](https://www.elastic.co/guide/cn/elasticsearch/guide/current/one-lang-fields.html)



#### [混合语言域](https://www.elastic.co/guide/cn/elasticsearch/guide/current/mixed-lang-fields.html)

```shell
PUT /movies
{
  "mappings": {
    "title": {
      "properties": {
        "title": { 
          "type": "string",
          "fields": {
            "de": { 
              "type":     "string",
              "analyzer": "german"
            },
            "en": { 
              "type":     "string",
              "analyzer": "english"
            },
            "fr": { 
              "type":     "string",
              "analyzer": "french"
            },
            "es": { 
              "type":     "string",
              "analyzer": "spanish"
            }
          }
        }
      }
    }
  }
}


PUT /movies
{
  "settings": {
    "analysis": {...} 
  },
  "mappings": {
    "title": {
      "properties": {
        "title": {
          "type": "string",
          "fields": {
            "de": {
              "type":     "string",
              "analyzer": "german"
            },
            "en": {
              "type":     "string",
              "analyzer": "english"
            },
            "fr": {
              "type":     "string",
              "analyzer": "french"
            },
            "es": {
              "type":     "string",
              "analyzer": "spanish"
            },
            "general": { 
              "type":     "string",
              "analyzer": "trigrams"
            }
          }
        }
      }
    }
  }
}


GET /movies/movie/_search
{
    "query": {
        "multi_match": {
            "query":    "club de la lucha",
            "fields": [ "title*^1.5", "title.general" ], 
            "type":     "most_fields",
            "minimum_should_match": "75%" 
        }
    }
}

```





### [词汇识别](https://www.elastic.co/guide/cn/elasticsearch/guide/current/identifying-words.html)



#### [标准分析器](https://www.elastic.co/guide/cn/elasticsearch/guide/current/standard-analyzer.html)

```shell
{
    "type":      "custom",
    "tokenizer": "standard",
    "filter":  [ "lowercase", "stop" ]
}
```



#### [标准分词器](https://www.elastic.co/guide/cn/elasticsearch/guide/current/standard-tokenizer.html)

```shell

# http://10.250.140.14:9200/_analyze?pretty=true&tokenizer=whitespace&text=You're the 1st runner home!


# http://10.250.140.14:9200/_analyze?pretty=true&tokenizer=standard&text=You're my 'favorite'.

# uax_url_email 分词器和 standard 分词器工作方式极其相同。 
# 区别只在于它能识别 email 地址和 URLs 并输出为单个语汇单元。
# http://10.250.140.14:9200/_analyze?pretty=true&tokenizer=standard&text=joe-bloggs@foo-bar.com
# http://10.250.140.14:9200/_analyze?pretty=true&tokenizer=uax_url_email&text=joe-bloggs@foo-bar.com
```



#### [整理输入文本](https://www.elastic.co/guide/cn/elasticsearch/guide/current/char-filters.html)

```shell
curl -XGET '10.250.140.14:9200/_analyze?pretty' -H 'Content-Type: application/json' -d '{
  "analyzer": "standard",
  "text": "<p>Some d&eacute;j&agrave; vu <a href=\"http://somedomain.com>\">website</a>"
}'


curl -XGET '10.250.140.14:9200/_analyze?pretty=true&analyzer=standard&char_filter=html_strip' -H 'Content-Type: application/json' -d '{
  "text": "<p>Some d&eacute;j&agrave; vu <a href=\"http://somedomain.com>\">website</a>"
}'



PUT /my_index
{
    "settings": {
        "analysis": {
            "analyzer": {
                "my_html_analyzer": {
                    "tokenizer":     "standard",
                    "char_filter": [ "html_strip" ]
                }
            }
        }
    }
}

GET /my_index/_analyze?analyzer=my_html_analyzer
<p>Some d&eacute;j&agrave; vu <a href="http://somedomain.com>">website</a>




PUT /my_index
{
  "settings": {
    "analysis": {
      "char_filter": { 
        "quotes": {
          "type": "mapping",
          "mappings": [ 
            "\\u0091=>\\u0027",
            "\\u0092=>\\u0027",
            "\\u2018=>\\u0027",
            "\\u2019=>\\u0027",
            "\\u201B=>\\u0027"
          ]
        }
      },
      "analyzer": {
        "quotes_analyzer": {
          "tokenizer":     "standard",
          "char_filter": [ "quotes" ] 
        }
      }
    }
  }
}


GET /my_index/_analyze?analyzer=quotes_analyzer
You’re my ‘favorite’ M‛Coy








```



### [归一化词元](https://www.elastic.co/guide/cn/elasticsearch/guide/current/token-normalization.html)

#### [举个例子](https://www.elastic.co/guide/cn/elasticsearch/guide/current/lowercase-token-filter.html)

```shell

http://10.250.140.14:9200/_analyze?pretty=true&tokenizer=standard&filter=lowercase&text=The QUICK Brown FOX!


PUT /my_index
{
  "settings": {
    "analysis": {
      "analyzer": {
        "my_lowercaser": {
          "tokenizer": "standard",
          "filter":  [ "lowercase" ]
        }
      }
    }
  }
}

GET /my_index/_analyze?analyzer=my_lowercaser
The QUICK Brown FOX! 




```





#### [如果有口音](https://www.elastic.co/guide/cn/elasticsearch/guide/current/asciifolding-token-filter.html)

```shell
PUT /my_index
{
  "settings": {
    "analysis": {
      "analyzer": {
        "folding": {
          "tokenizer": "standard",
          "filter":  [ "lowercase", "asciifolding" ]
        }
      }
    }
  }
}

GET /my_index?analyzer=folding
My œsophagus caused a débâcle




PUT /my_index/_mapping/my_type
{
  "properties": {
    "title": { 
      "type":           "string",
      "analyzer":       "standard",
      "fields": {
        "folded": { 
          "type":       "string",
          "analyzer":   "folding"
        }
      }
    }
  }
}
GET /my_index/_analyze?field=title 
Esta está loca

GET /my_index/_analyze?field=title.folded 
Esta está loca



PUT /my_index/my_type/1
{ "title": "Esta loca!" }

PUT /my_index/my_type/2
{ "title": "Está loca!" }

GET /my_index/_search
{
  "query": {
    "multi_match": {
      "type":     "most_fields",
      "query":    "esta loca",
      "fields": [ "title", "title.folded" ]
    }
  }
}


GET /my_index/_validate/query?explain
{
  "query": {
    "multi_match": {
      "type":     "most_fields",
      "query":    "está loca",
      "fields": [ "title", "title.folded" ]
    }
  }
}
```



#### [Unicode的世界](https://www.elastic.co/guide/cn/elasticsearch/guide/current/unicode-normalization.html)

```shell
PUT /my_index
{
  "settings": {
    "analysis": {
      "filter": {
        "nfkc_normalizer": { 
          "type": "icu_normalizer",
          "name": "nfkc"
        }
      },
      "analyzer": {
        "my_normalizer": {
          "tokenizer": "icu_tokenizer",
          "filter":  [ "nfkc_normalizer" ]
        }
      }
    }
  }
}
```





#### [Unicode 大小写折叠](https://www.elastic.co/guide/cn/elasticsearch/guide/current/case-folding.html)

```shell
PUT /my_index
{
  "settings": {
    "analysis": {
      "analyzer": {
        "my_lowercaser": {
          "tokenizer": "icu_tokenizer",
          "filter":  [ "icu_normalizer" ] 
        }
      }
    }
  }
}

GET /_analyze?analyzer=standard 
Weißkopfseeadler WEISSKOPFSEEADLER

GET /my_index/_analyze?analyzer=my_lowercaser 
Weißkopfseeadler WEISSKOPFSEEADLER
```







#### [Unicode 字符折叠](https://www.elastic.co/guide/cn/elasticsearch/guide/current/character-folding.html)

```shell
PUT /my_index
{
  "settings": {
    "analysis": {
      "analyzer": {
        "my_folder": {
          "tokenizer": "icu_tokenizer",
          "filter":  [ "icu_folding" ]
        }
      }
    }
  }
}

GET /my_index/_analyze?analyzer=my_folder
١٢٣٤٥ 
```



#### [排序和整理](https://www.elastic.co/guide/cn/elasticsearch/guide/current/sorting-collations.html)



```shell
PUT /my_index
{
  "mappings": {
    "user": {
      "properties": {
        "name": { 
          "type": "string",
          "fields": {
            "raw": { 
              "type":  "string",
              "index": "not_analyzed"
            }
          }
        }
      }
    }
  }
}

PUT /my_index/user/1
{ "name": "Boffey" }

PUT /my_index/user/2
{ "name": "BROWN" }

PUT /my_index/user/3
{ "name": "bailey" }

GET /my_index/user/_search?sort=name.raw



#########################################
PUT /my_index
{
  "settings": {
    "analysis": {
      "analyzer": {
        "case_insensitive_sort": {
          "tokenizer": "keyword",    
          "filter":  [ "lowercase" ] 
        }
      }
    }
  }
}


PUT /my_index/_mapping/user
{
  "properties": {
    "name": {
      "type": "string",
      "fields": {
        "lower_case_sort": { 
          "type":     "string",
          "analyzer": "case_insensitive_sort"
        }
      }
    }
  }
}

PUT /my_index/user/1
{ "name": "Boffey" }

PUT /my_index/user/2
{ "name": "BROWN" }

PUT /my_index/user/3
{ "name": "bailey" }

GET /my_index/user/_search?sort=name.lower_case_sort
```



### [将单词还原为词根](https://www.elastic.co/guide/cn/elasticsearch/guide/current/stemming.html)



#### [词干提取算法](https://www.elastic.co/guide/cn/elasticsearch/guide/current/algorithmic-stemmers.html)



```shell
{
  "settings": {
    "analysis": {
      "filter": {
        "english_stop": {
          "type":       "stop",
          "stopwords":  "_english_"
        },
        "english_keywords": {
          "type":       "keyword_marker", 
          "keywords":   []
        },
        "english_stemmer": {
          "type":       "stemmer",
          "language":   "english" 
        },
        "english_possessive_stemmer": {
          "type":       "stemmer",
          "language":   "possessive_english" 
        }
      },
      "analyzer": {
        "english": {
          "tokenizer":  "standard",
          "filter": [
            "english_possessive_stemmer",
            "lowercase",
            "english_stop",
            "english_keywords",
            "english_stemmer"
          ]
        }
      }
    }
  }
}


PUT /my_index
{
  "settings": {
    "analysis": {
      "filter": {
        "english_stop": {
          "type":       "stop",
          "stopwords":  "_english_"
        },
        "light_english_stemmer": {
          "type":       "stemmer",
          "language":   "light_english" 
        },
        "english_possessive_stemmer": {
          "type":       "stemmer",
          "language":   "possessive_english"
        }
      },
      "analyzer": {
        "english": {
          "tokenizer":  "standard",
          "filter": [
            "english_possessive_stemmer",
            "lowercase",
            "english_stop",
            "light_english_stemmer", 
            "asciifolding" 
          ]
        }
      }
    }
  }
}
```





#### [字典词干提取器](https://www.elastic.co/guide/cn/elasticsearch/guide/current/dictionary-stemmers.html)







#### [Hunspell 词干提取器](https://www.elastic.co/guide/cn/elasticsearch/guide/current/hunspell.html)



#### [选择一个词干提取器](https://www.elastic.co/guide/cn/elasticsearch/guide/current/choosing-a-stemmer.html)



#### [控制词干提取](https://www.elastic.co/guide/cn/elasticsearch/guide/current/controlling-stemming.html)

```SHELL
PUT /my_index
{
  "settings": {
    "analysis": {
      "filter": {
        "no_stem": {
          "type": "keyword_marker",
          "keywords": [ "skies" ] 
        }
      },
      "analyzer": {
        "my_english": {
          "tokenizer": "standard",
          "filter": [
            "lowercase",
            "no_stem",
            "porter_stem"
          ]
        }
      }
    }
  }
}

GET /my_index/_analyze?analyzer=my_english
sky skies skiing skis 


PUT /my_index
{
  "settings": {
    "analysis": {
      "filter": {
        "custom_stem": {
          "type": "stemmer_override",
          "rules": [ 
            "skies=>sky",
            "mice=>mouse",
            "feet=>foot"
          ]
        }
      },
      "analyzer": {
        "my_english": {
          "tokenizer": "standard",
          "filter": [
            "lowercase",
            "custom_stem", 
            "porter_stem"
          ]
        }
      }
    }
  }
}

GET /my_index/_analyze?analyzer=my_english
The mice came down from the skies and ran over my feet 
```



#### [原形词干提取](https://www.elastic.co/guide/cn/elasticsearch/guide/current/stemming-in-situ.html)

```
PUT /my_index
{
  "settings": {
    "analysis": {
      "filter": {
        "unique_stem": {
          "type": "unique",
          "only_on_same_position": true 
        }
      },
      "analyzer": {
        "in_situ": {
          "tokenizer": "standard",
          "filter": [
            "lowercase",
            "keyword_repeat", 
            "porter_stem",
            "unique_stem" 
          ]
        }
      }
    }
  }
}
```



### [停用词: 性能与精度](https://www.elastic.co/guide/cn/elasticsearch/guide/current/stopwords.html)

#### [停用词的优缺点](https://www.elastic.co/guide/cn/elasticsearch/guide/current/pros-cons-stopwords.html)



#### [使用停用词](https://www.elastic.co/guide/cn/elasticsearch/guide/current/using-stopwords.html)

```shell
PUT /my_index
{
  "settings": {
    "analysis": {
      "analyzer": {
        "my_analyzer": { 
          "type": "standard", 
          "stopwords": [ "and", "the" ] 
        }
      }
    }
  }
}


GET /my_index/_analyze?analyzer=my_analyzer
The quick and the dead

# 禁用停止词
PUT /my_index
{
  "settings": {
    "analysis": {
      "analyzer": {
        "my_english": {
          "type":      "english", 
          "stopwords": "_none_" 
        }
      }
    }
  }
}


PUT /my_index
{
  "settings": {
    "analysis": {
      "analyzer": {
        "my_english": {
          "type":           "english",
          "stopwords_path": "stopwords/english.txt" 
        }
      }
    }
  }
}


PUT /my_index
{
  "settings": {
    "analysis": {
      "filter": {
        "spanish_stop": {
          "type":        "stop",
          "stopwords": [ "si", "esta", "el", "la" ]  
        },
        "light_spanish": { 
          "type":     "stemmer",
          "language": "light_spanish"
        }
      },
      "analyzer": {
        "my_spanish": {
          "tokenizer": "spanish",
          "filter": [ 
            "lowercase",
            "asciifolding",
            "spanish_stop",
            "light_spanish"
          ]
        }
      }
    }
  }
}
```





#### [停用词与性能](https://www.elastic.co/guide/cn/elasticsearch/guide/current/stopwords-performance.html)



#### [词项的分别管理](https://www.elastic.co/guide/cn/elasticsearch/guide/current/common-terms.html)

查询字符串中的词项可以分为更重要（低频词）和次重要（高频词）这两类



```shell
# 让所有低频词都必须匹配，而只对那些包括超过 75% 的高频词文档进行评分
{
  "common": {
    "text": {
      "query":                  "Quick and the dead",
      "cutoff_frequency":       0.01,
      "low_freq_operator":      "and",
      "minimum_should_match": {
        "high_freq":            "75%"
      }
    }
  }
}



```



#### [停用词与短语查询](https://www.elastic.co/guide/cn/elasticsearch/guide/current/stopwords-phrases.html)

```shell
PUT /my_index
{
  "mappings": {
    "my_type": {
      "properties": {
        "title": { 
          "type":          "string"
       },
        "content": { 
          "type":          "string",
          "index_options": "freqs"
      }
    }
  }
}
```





#### [common_grams 过滤器](https://www.elastic.co/guide/cn/elasticsearch/guide/current/common-grams.html)

```shell
PUT /my_index
{
  "settings": {
    "analysis": {
      "filter": {
        "index_filter": { 
          "type":         "common_grams",
          "common_words": "_english_" 
        },
        "search_filter": { 
          "type":         "common_grams",
          "common_words": "_english_", 
          "query_mode":   true
        }
      },
      "analyzer": {
        "index_grams": { 
          "tokenizer":  "standard",
          "filter":   [ "lowercase", "index_filter" ]
        },
        "search_grams": { 
          "tokenizer": "standard",
          "filter":  [ "lowercase", "search_filter" ]
        }
      }
    }
  }
}

PUT /my_index/_mapping/my_type
{
  "properties": {
    "text": {
      "type":            "string",
      "analyzer":  "index_grams", 
      "search_analyzer": "standard" 
    }
  }
}


GET /my_index/_search
{
  "query": {
    "match_phrase": {
      "text": {
        "query":    "The quick and brown fox",
        "analyzer": "search_grams" 
      }
    }
  }
}


GET /my_index/_search
{
  "query": {
    "match_phrase": {
      "text": {
        "query":    "The quick",
        "analyzer": "search_grams"
      }
    }
  }
}
```











#### [停用词与相关性](https://www.elastic.co/guide/cn/elasticsearch/guide/current/stopwords-relavance.html)





### [同义词](https://www.elastic.co/guide/cn/elasticsearch/guide/current/synonyms.html)

词干提取是通过简化他们的词根形式来扩大搜索的范围，同义词 通过相关的观念和概念来扩大搜索范围。 

####  [使用同义词](https://www.elastic.co/guide/cn/elasticsearch/guide/current/using-synonyms.html)

```shell
PUT /my_index
{
  "settings": {
    "analysis": {
      "filter": {
        "my_synonym_filter": {
          "type": "synonym", 
          "synonyms": [ 
            "british,english",
            "queen,monarch"
          ]
        }
      },
      "analyzer": {
        "my_synonyms": {
          "tokenizer": "standard",
          "filter": [
            "lowercase",
            "my_synonym_filter" 
          ]
        }
      }
    }
  }
}


GET /my_index/_analyze?analyzer=my_synonyms
Elizabeth is the English queen
```



#### [同义词格式](https://www.elastic.co/guide/cn/elasticsearch/guide/current/synonym-formats.html)



#### [同义词和分析链](https://www.elastic.co/guide/cn/elasticsearch/guide/current/synonyms-analysis-chain.html)



#### [多词同义词和短语查询](https://www.elastic.co/guide/cn/elasticsearch/guide/current/multi-word-synonyms.html)

```shell
PUT /my_index
{
  "settings": {
    "analysis": {
      "filter": {
        "my_synonym_filter": {
          "type": "synonym",
          "synonyms": [
            "usa,united states,u s a,united states of america"
          ]
        }
      },
      "analyzer": {
        "my_synonyms": {
          "tokenizer": "standard",
          "filter": [
            "lowercase",
            "my_synonym_filter"
          ]
        }
      }
    }
  }
}

GET /my_index/_analyze?analyzer=my_synonyms&text=
The United States is wealthy


GET /my_index/_validate/query?explain
{
  "query": {
    "match_phrase": {
      "text": {
        "query": "usa is wealthy",
        "analyzer": "my_synonyms"
      }
    }
  }
}



# 使用简单收缩进行短语查询
PUT /my_index
{
  "settings": {
    "analysis": {
      "filter": {
        "my_synonym_filter": {
          "type": "synonym",
          "synonyms": [
            "united states,u s a,united states of america=>usa"
          ]
        }
      },
      "analyzer": {
        "my_synonyms": {
          "tokenizer": "standard",
          "filter": [
            "lowercase",
            "my_synonym_filter"
          ]
        }
      }
    }
  }
}

GET /my_index/_analyze?analyzer=my_synonyms
The United States is wealthy
```





#### [符号同义词](https://www.elastic.co/guide/cn/elasticsearch/guide/current/symbol-synonyms.html)

```shell
PUT /my_index
{
  "settings": {
    "analysis": {
      "char_filter": {
        "emoticons": {
          "type": "mapping",
          "mappings": [ 
            ":)=>emoticon_happy",
            ":(=>emoticon_sad"
          ]
        }
      },
      "analyzer": {
        "my_emoticons": {
          "char_filter": "emoticons",
          "tokenizer":   "standard",
          "filter":    [ "lowercase" ]
          ]
        }
      }
    }
  }
}

GET /my_index/_analyze?analyzer=my_emoticons
I am :) not :( 
```



### [拼写错误](https://www.elastic.co/guide/cn/elasticsearch/guide/current/fuzzy-matching.html)

#### [模糊性](https://www.elastic.co/guide/cn/elasticsearch/guide/current/fuzziness.html)





#### [模糊查询](https://www.elastic.co/guide/cn/elasticsearch/guide/current/fuzzy-query.html)

```
POST /my_index/my_type/_bulk
{ "index": { "_id": 1 }}
{ "text": "Surprise me!"}
{ "index": { "_id": 2 }}
{ "text": "That was surprising."}
{ "index": { "_id": 3 }}
{ "text": "I wasn't surprised."}

GET /my_index/my_type/_search
{
  "query": {
    "fuzzy": {
      "text": "surprize"
    }
  }
}

GET /my_index/my_type/_search
{
  "query": {
    "fuzzy": {
      "text": {
        "value": "surprize",
        "fuzziness": 1
      }
    }
  }
}
```



#### [模糊匹配查询](https://www.elastic.co/guide/cn/elasticsearch/guide/current/fuzzy-match-query.html)

```
GET /my_index/my_type/_search
{
  "query": {
    "match": {
      "text": {
        "query":     "SURPRIZE ME!",
        "fuzziness": "AUTO",
        "operator":  "and"
      }
    }
  }
}


GET /my_index/my_type/_search
{
  "query": {
    "multi_match": {
      "fields":  [ "text", "title" ],
      "query":     "SURPRIZE ME!",
      "fuzziness": "AUTO"
    }
  }
}
```





#### [模糊性评分](https://www.elastic.co/guide/cn/elasticsearch/guide/current/fuzzy-scoring.html)



## [聚合](https://www.elastic.co/guide/cn/elasticsearch/guide/current/aggregations.html)



### [高阶概念](https://www.elastic.co/guide/cn/elasticsearch/guide/current/aggs-high-level.html)

#### [桶](https://www.elastic.co/guide/cn/elasticsearch/guide/current/_buckets.html)

#### [指标](https://www.elastic.co/guide/cn/elasticsearch/guide/current/_metrics.html)

#### [桶和指标的组合](https://www.elastic.co/guide/cn/elasticsearch/guide/current/_combining_the_two.html)



### [尝试聚合](https://www.elastic.co/guide/cn/elasticsearch/guide/current/_aggregation_test_drive.html)



```shell
curl -XPOST '10.250.140.14:9200/cars/transactions/_bulk?pretty' -H 'Content-Type: application/json' -d'
{ "index": {}}
{ "price" : 10000, "color" : "red", "make" : "honda", "sold" : "2014-10-28" }
{ "index": {}}
{ "price" : 20000, "color" : "red", "make" : "honda", "sold" : "2014-11-05" }
{ "index": {}}
{ "price" : 30000, "color" : "green", "make" : "ford", "sold" : "2014-05-18" }
{ "index": {}}
{ "price" : 15000, "color" : "blue", "make" : "toyota", "sold" : "2014-07-02" }
{ "index": {}}
{ "price" : 12000, "color" : "green", "make" : "toyota", "sold" : "2014-08-19" }
{ "index": {}}
{ "price" : 20000, "color" : "red", "make" : "honda", "sold" : "2014-11-05" }
{ "index": {}}
{ "price" : 80000, "color" : "red", "make" : "bmw", "sold" : "2014-01-01" }
{ "index": {}}
{ "price" : 25000, "color" : "blue", "make" : "ford", "sold" : "2014-02-12" }
'

curl -XGET '10.250.140.14:9200/cars/transactions/_search?pretty'  -d '{
    "size" : 0,
    "aggs" : { 
        "popular_colors" : { 
            "terms" : { 
              "field" : "color.keyword"
            }
        }
    }
}'
```

​	



#### [添加度量指标](https://www.elastic.co/guide/cn/elasticsearch/guide/current/_adding_a_metric_to_the_mix.html)		

```shell
curl -XGET '10.250.140.14:9200/cars/transactions/_search?pretty'  -d '{
   "size" : 0,
   "aggs": {
      "colors": {
         "terms": {
            "field": "color.keyword"
         },
         "aggs": { 
            "avg_price": { 
               "avg": {
                  "field": "price" 
               }
            }
         }
      }
   }
}'
```

#### [嵌套桶](https://www.elastic.co/guide/cn/elasticsearch/guide/current/_buckets_inside_buckets.html)

```shell
curl -XGET '10.250.140.14:9200/cars/transactions/_search?pretty'  -d '{
   "size" : 0,
   "aggs": {
      "colors": {
         "terms": {
            "field": "color.keyword"
         },
         "aggs": {
            "avg_price": { 
               "avg": {
                  "field": "price"
               }
            },
            "make": { 
                "terms": {
                    "field": "make.keyword" 
                }
            }
         }
      }
   }
}'




curl -XGET '10.250.140.14:9200/cars/transactions/_search?pretty'  -d '{
   "size" : 0,
   "aggs": {
      "colors": {
         "terms": {
            "field": "color.keyword"
         },
         "aggs": {
            "avg_price": { 
               "avg": {
                  "field": "price"
               }
            },
            "make": { 
                "terms": {
                    "field": "make.keyword" 
                },
                "aggs": {
                  "avg_make_price": { 
                     "avg": {
                        "field": "price"
                     }
                  },
                  "min_price" : { "min": { "field": "price"} }, 
                  "max_price" : { "max": { "field": "price"} } 
              	}
            }
         }
      }
   }
}'
```



### [条形图](https://www.elastic.co/guide/cn/elasticsearch/guide/current/_building_bar_charts.html)

```shell
# 每个售价区间内汽车所带来的收入，
# 可以通过对每个区间内已售汽车的售价求和得到
curl -XGET '10.250.140.14:9200/cars/transactions/_search?pretty'  -d '
{
   "size" : 0,
   "aggs":{
      "price":{
         "histogram":{ 
            "field": "price",
            "interval": 20000
         },
         "aggs":{
            "revenue": {
               "sum": { 
                 "field" : "price"
               }
             }
         }
      }
   }
}'


curl -XGET '10.250.140.14:9200/cars/transactions/_search?pretty'  -d '
{
  "size" : 0,
  "aggs": {
    "makes": {
      "terms": {
        "field": "make.keyword",
        "size": 10
      },
      "aggs": {
        "stats": {
          "extended_stats": {
            "field": "price"
          }
        }
      }
    }
  }
}'
```



### [按时间统计](https://www.elastic.co/guide/cn/elasticsearch/guide/current/_looking_at_time.html)

```shell
curl -XGET '10.250.140.14:9200/cars/transactions/_search?pretty'  -d '
{
   "size" : 0,
   "aggs": {
      "sales": {
         "date_histogram": {
            "field": "sold",
            "interval": "month", 
            "format": "yyyy-MM-dd" 
         }
      }
   }
}'
```









#### [返回空 Buckets](https://www.elastic.co/guide/cn/elasticsearch/guide/current/_returning_empty_buckets.html)

```shell
curl -XGET '10.250.140.14:9200/cars/transactions/_search?pretty' -H 'Content-Type: application/json' -d'
{
   "size" : 0,
   "aggs": {
      "sales": {
         "date_histogram": {
            "field": "sold",
            "interval": "month",
            "format": "yyyy-MM-dd",
            "min_doc_count" : 0, 
            "extended_bounds" : { 
                "min" : "2014-01-01",
                "max" : "2015-12-31"
            }
         }
      }
   }
}
'
```





聚合以便按季度展示所有汽车品牌总销售额。

同时按季度、按每个汽车品牌计算销售总额，以便可以找出哪种品牌最赚钱

```shell
curl -XGET '10.250.140.14:9200/cars/transactions/_search?pretty' -H 'Content-Type: application/json' -d '{
   "size" : 0,
   "aggs": {
      "sales": {
         "date_histogram": {
            "field": "sold",
            "interval": "quarter", 
            "format": "yyyy-MM-dd",
            "min_doc_count" : 0,
            "extended_bounds" : {
                "min" : "2014-01-01",
                "max" : "2014-12-31"
            }
         },
         "aggs": {
            "per_make_sum": {
               "terms": {
                  "field": "make.keyword"
               },
               "aggs": {
                  "sum_price": {
                     "sum": { "field": "price" } 
                  }
               }
            },
            "total_sum": {
               "sum": { "field": "price" } 
            }
         }
      }
   }
}'
```

### [范围限定的聚合](https://www.elastic.co/guide/cn/elasticsearch/guide/current/_scoping_aggregations.html)

```shell
curl -XGET '10.250.140.14:9200/cars/transactions/_search?pretty' -H 'Content-Type: application/json' -d '{
    "size" : 0,
    "aggs" : {
        "colors" : {
            "terms" : {
              "field" : "color"
            }
        }
    }
}'



curl -XGET '10.250.140.14:9200/cars/transactions/_search?pretty' -H 'Content-Type: application/json' -d '{
    "query" : {
        "match" : {
            "make" : "ford"
        }
    },
    "aggs" : {
        "colors" : {
            "terms" : {
              "field" : "color.keyword"
            }
        }
    }
}'


# 全局桶
curl -XGET '10.250.140.14:9200/cars/transactions/_search?pretty' -H 'Content-Type: application/json' -d '{
    "size" : 0,
    "query" : {
        "match" : {
            "make" : "ford"
        }
    },
    "aggs" : {
        "single_avg_price": {
            "avg" : { "field" : "price" } 
        },
        "all": {
            "global" : {}, 
            "aggs" : {
                "avg_price": {
                    "avg" : { "field" : "price" } 
                }

            }
        }
    }
}'
```

​			

### [过滤和聚合](https://www.elastic.co/guide/cn/elasticsearch/guide/current/_filtering_queries_and_aggregations.html)



#### [过滤](https://www.elastic.co/guide/cn/elasticsearch/guide/current/_filtering_queries.html)

```shell
curl -XGET '10.250.140.14:9200/cars/transactions/_search?pretty' -H 'Content-Type: application/json' -d '{
    "size" : 0,
    "query" : {
        "constant_score": {
            "filter": {
                "range": {
                    "price": {
                        "gte": 10000
                    }
                }
            }
        }
    },
    "aggs" : {
        "single_avg_price": {
            "avg" : { "field" : "price" }
        }
    }
}'
```

#### [过滤桶](https://www.elastic.co/guide/cn/elasticsearch/guide/current/_filter_bucket.html)

```shell
curl -XGET '10.250.140.14:9200/cars/transactions/_search?pretty' -H 'Content-Type: application/json' -d '{
   "size" : 0,
   "query":{
      "match": {
         "make": "ford"
      }
   },
   "aggs":{
      "recent_sales": {
         "filter": { 
            "range": {
               "sold": {
                  "from": "now-1M"
               }
            }
         },
         "aggs": {
            "average_price":{
               "avg": {
                  "field": "price" 
               }
            }
         }
      }
   }
}'
```



#### [post_filter](https://www.elastic.co/guide/cn/elasticsearch/guide/current/_post_filter.html)

```shell
curl -XGET '10.250.140.14:9200/cars/transactions/_search?pretty' -H 'Content-Type: application/json' -d '{
    "size" : 0,
    "query": {
        "match": {
            "make": "ford"
        }
    },
    "post_filter": {    
        "term" : {
            "color" : "green"
        }
    },
    "aggs" : {
        "all_colors": {
            "terms" : { "field" : "color.keyword" }
        }
    }
}'
```

#### [小结](https://github.com/elasticsearch-cn/elasticsearch-definitive-guide/edit/cn/300_Aggregations/45_filtering.asciidoc)

选择合适类型的过滤（如：搜索命中、聚合或两者兼有）通常和我们期望如何表现用户交互有关。选择合适的过滤器（或组合）取决于我们期望如何将结果呈现给用户。

- 在 `filter` 过滤中的 `non-scoring` 查询，同时影响搜索结果和聚合结果。
- `filter` 桶影响聚合。
- `post_filter` 只影响搜索结果。



### [多桶排序](https://www.elastic.co/guide/cn/elasticsearch/guide/current/_sorting_multivalue_buckets.html)



#### [内置排序](https://www.elastic.co/guide/cn/elasticsearch/guide/current/_intrinsic_sorts.html)

```shell
curl -XGET '10.250.140.14:9200/cars/transactions/_search?pretty' -H 'Content-Type: application/json' -d '{
    "size" : 0,
    "aggs" : {
        "colors" : {
            "terms" : {
              "field" : "color.keyword",
              "order": {
                "_count" : "asc" 
              }
            }
        }
    }
}'
```



#### [按度量排序](https://www.elastic.co/guide/cn/elasticsearch/guide/current/_sorting_by_a_metric.html)

```shell
curl -XGET '10.250.140.14:9200/cars/transactions/_search?pretty' -H 'Content-Type: application/json' -d '{
    "size" : 0,
    "aggs" : {
        "colors" : {
            "terms" : {
              "field" : "color.keyword",
              "order": {
                "avg_price" : "asc" 
              }
            },
            "aggs": {
                "avg_price": {
                    "avg": {"field": "price"} 
                }
            }
        }
    }
}'



curl -XGET '10.250.140.14:9200/cars/transactions/_search?pretty' -H 'Content-Type: application/json' -d '{
    "size" : 0,
    "aggs" : {
        "colors" : {
            "terms" : {
              "field" : "color.keyword",
              "order": {
                "stats.variance" : "asc" 
              }
            },
            "aggs": {
                "stats": {
                    "extended_stats": {"field": "price"}
                }
            }
        }
    }
}'

```









#### [基于“深度”度量排序](https://www.elastic.co/guide/cn/elasticsearch/guide/current/_sorting_based_on_deep_metrics.html)

```shell
curl -XGET '10.250.140.14:9200/cars/transactions/_search?pretty' -H 'Content-Type: application/json' -d'
{
    "size" : 0,
    "aggs" : {
        "colors" : {
            "histogram" : {
              "field" : "price",
              "interval": 20000,
              "order": {
                "red_green_cars>stats.variance" : "asc" 
              }
            },
            "aggs": {
                "red_green_cars": {
                    "filter": { "terms": {"color": ["red", "green"]}}, 
                    "aggs": {
                        "stats": {"extended_stats": {"field" : "price"}} 
                    }
                }
            }
        }
    }
}
'



curl -XGET '10.250.140.14:9200/cars/transactions/_search?pretty' -H 'Content-Type: application/json' -d'
{
    "size" : 0,
    "aggs" : {
        "colors" : {
            "histogram" : {
              "field" : "price",
              "interval": 20000
            },
            "aggs": {
                "red_green_cars": {
                    "filter": { "terms": {"color": ["red", "green"]}}, 
                    "aggs": {
                        "stats": {"extended_stats": {"field" : "price"}} 
                    }
                }
            }
        }
    }
}
'
```



### [近似聚合](https://www.elastic.co/guide/cn/elasticsearch/guide/current/_approximate_aggregations.html)

#### [统计去重后的数量](https://www.elastic.co/guide/cn/elasticsearch/guide/current/cardinality.html)

```shell
curl -XGET '10.250.140.14:9200/cars/transactions/_search?pretty' -H 'Content-Type: application/json' -d '{
    "size" : 0,
    "aggs" : {
        "distinct_colors" : {
            "cardinality" : {
              "field" : "color.keyword"
            }
        }
    }
}'


curl -XGET '10.250.140.14:9200/cars/transactions/_search?pretty' -H 'Content-Type: application/json' -d '{
  "size" : 0,
  "aggs" : {
      "months" : {
        "date_histogram": {
          "field": "sold",
          "interval": "month"
        },
        "aggs": {
          "distinct_colors" : {
              "cardinality" : {
                "field" : "color.keyword"
              }
          }
        }
      }
  }
}'

# precision_threshold   适用于高基数和长字符串

curl -XGET '10.250.140.14:9200/cars/transactions/_search?pretty' -H 'Content-Type: application/json' -d '{
    "size" : 0,
    "aggs" : {
        "distinct_colors" : {
            "cardinality" : {
              "field" : "color.keyword",
              "precision_threshold" : 100 
            }
        }
    }
}'

# Mapper Murmur3 Pluginedit
# https://www.elastic.co/guide/en/elasticsearch/plugins/current/mapper-murmur3.html
# https://artifacts.elastic.co/downloads/elasticsearch-plugins/mapper-murmur3/mapper-murmur3-6.2.3.zip.
curl -XDELETE '10.250.140.14:9200/cars?pretty'

curl -XPUT '10.250.140.14:9200/cars?pretty' -d '{
  "mappings": {
    "transactions": {
      "properties": {
        "color": {
          "type": "string",
          "fields": {
            "hash": {
              "type": "murmur3" 
            }
          }
        }
      }
    }
  }
}'


curl -XPUT '10.250.140.14:9200/cars/transactions/_bulk' -d '
{ "index": {}}
{ "price" : 10000, "color" : "red", "make" : "honda", "sold" : "2014-10-28" }
{ "index": {}}
{ "price" : 20000, "color" : "red", "make" : "honda", "sold" : "2014-11-05" }
{ "index": {}}
{ "price" : 30000, "color" : "green", "make" : "ford", "sold" : "2014-05-18" }
{ "index": {}}
{ "price" : 15000, "color" : "blue", "make" : "toyota", "sold" : "2014-07-02" }
{ "index": {}}
{ "price" : 12000, "color" : "green", "make" : "toyota", "sold" : "2014-08-19" }
{ "index": {}}
{ "price" : 20000, "color" : "red", "make" : "honda", "sold" : "2014-11-05" }
{ "index": {}}
{ "price" : 80000, "color" : "red", "make" : "bmw", "sold" : "2014-01-01" }
{ "index": {}}
{ "price" : 25000, "color" : "blue", "make" : "ford", "sold" : "2014-02-12" }'

curl -XGET '10.250.140.14:9200/cars/transactions/_search?pretty' -d '{
    "size" : 0,
    "aggs" : {
        "distinct_colors" : {
            "cardinality" : {
              "field" : "color.hash" 
            }
        }
    }
}'
```







#### [百分位计算](https://www.elastic.co/guide/cn/elasticsearch/guide/current/percentiles.html)

```shell
curl -XPUT '10.250.140.14:9200/website/logs/_bulk?pretty' -d '
{ "index": {}}
{ "latency" : 100, "zone" : "US", "timestamp" : "2014-10-28" }
{ "index": {}}
{ "latency" : 80, "zone" : "US", "timestamp" : "2014-10-29" }
{ "index": {}}
{ "latency" : 99, "zone" : "US", "timestamp" : "2014-10-29" }
{ "index": {}}
{ "latency" : 102, "zone" : "US", "timestamp" : "2014-10-28" }
{ "index": {}}
{ "latency" : 75, "zone" : "US", "timestamp" : "2014-10-28" }
{ "index": {}}
{ "latency" : 82, "zone" : "US", "timestamp" : "2014-10-29" }
{ "index": {}}
{ "latency" : 100, "zone" : "EU", "timestamp" : "2014-10-28" }
{ "index": {}}
{ "latency" : 280, "zone" : "EU", "timestamp" : "2014-10-29" }
{ "index": {}}
{ "latency" : 155, "zone" : "EU", "timestamp" : "2014-10-29" }
{ "index": {}}
{ "latency" : 623, "zone" : "EU", "timestamp" : "2014-10-28" }
{ "index": {}}
{ "latency" : 380, "zone" : "EU", "timestamp" : "2014-10-28" }
{ "index": {}}
{ "latency" : 319, "zone" : "EU", "timestamp" : "2014-10-29" }'


curl -XGET '10.250.140.14:9200/website/logs/_search?pretty' -d '{
    "size" : 0,
    "aggs" : {
        "load_times" : {
            "percentiles" : {
                "field" : "latency" 
            }
        },
        "avg_load_time" : {
            "avg" : {
                "field" : "latency" 
            }
        }
    }
}'

# 延时的分布很广，看看它们是否与数据中心的地理区域有关

curl -XGET '10.250.140.14:9200/website/logs/_search?pretty' -d '{
    "size" : 0,
    "aggs" : {
        "zones" : {
            "terms" : {
                "field" : "zone.keyword" 
            },
            "aggs" : {
                "load_times" : {
                    "percentiles" : { 
                      "field" : "latency",
                      "percents" : [50, 95.0, 99.0] 
                    }
                },
                "load_avg" : {
                    "avg" : {
                        "field" : "latency"
                    }
                }
            }
        }
    }
}'



# 百分位等级

curl -XGET '10.250.140.14:9200/website/logs/_search?pretty' -d '{
    "size" : 0,
    "aggs" : {
        "zones" : {
            "terms" : {
                "field" : "zone.keyword"
            },
            "aggs" : {
                "load_times" : {
                    "percentile_ranks" : {
                      "field" : "latency",
                      "values" : [210, 800] 
                    }
                }
            }
        }
    }
}'
```



### [通过聚合发现异常指标](https://www.elastic.co/guide/cn/elasticsearch/guide/current/significant-terms.html)





### [Doc Values and Fielddata](https://www.elastic.co/guide/cn/elasticsearch/guide/current/docvalues-and-fielddata.html)



#### [Doc Values](https://www.elastic.co/guide/cn/elasticsearch/guide/current/docvalues.html)

```shell
curl -XGET '10.250.140.14:9200/my_index/_search?pretty' -d '{
  "query" : {
    "match" : {
      "body" : "brown"
    }
  },
  "aggs" : {
    "popular_terms": {
      "terms" : {
        "field" : "body"
      }
    }
  }
}'
```





#### [聚合与分析](https://www.elastic.co/guide/cn/elasticsearch/guide/current/aggregations-and-analysis.html)



```shell

curl -XPOST '10.250.140.14:9200/agg_analysis/data/_bulk?pretty' -d '{ "index": {}}
{ "state" : "New York" }
{ "index": {}}
{ "state" : "New Jersey" }
{ "index": {}}
{ "state" : "New Mexico" }
{ "index": {}}
{ "state" : "New York" }
{ "index": {}}
{ "state" : "New York" }'


curl -XGET '10.250.140.14:9200/agg_analysis/data/_search?pretty' -d '
{
    "size" : 0,
    "aggs" : {
        "states" : {
            "terms" : {
                "field" : "state.keyword"
            }
        }
    }
}'
```



#### [限制内存使用](https://www.elastic.co/guide/cn/elasticsearch/guide/current/_limiting_memory_usage.html)



#### [预加载 fielddata](https://www.elastic.co/guide/cn/elasticsearch/guide/current/preload-fielddata.html)



#### [优化聚合查询](https://www.elastic.co/guide/cn/elasticsearch/guide/current/_preventing_combinatorial_explosions.html)



### [总结](https://www.elastic.co/guide/cn/elasticsearch/guide/current/_closing_thoughts.html)





**Elasticsearch: 权威指南：**<https://www.elastic.co/guide/cn/elasticsearch/guide/current/index.html>