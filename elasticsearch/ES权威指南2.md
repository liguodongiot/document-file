## [地理位置](https://www.elastic.co/guide/cn/elasticsearch/guide/current/geoloc.html)



### [地理坐标点](https://www.elastic.co/guide/cn/elasticsearch/guide/current/geopoints.html)

地理坐标点不能被动态映射自动检测，而是需要显式声明对应字段类型为 `geo-point`

```shell
PUT /attractions
{
  "mappings": {
    "restaurant": {
      "properties": {
        "name": {
          "type": "text"
        },
        "location": {
          "type": "geo_point"
        }
      }
    }
  }
}
```



#### [经纬度坐标格式](https://www.elastic.co/guide/cn/elasticsearch/guide/current/lat-lon-formats.html)

```shell
PUT /attractions/restaurant/1
{
  "name":     "Chipotle Mexican Grill",
  "location": "40.715, -74.011" 
}

PUT /attractions/restaurant/2
{
  "name":     "Pala Pizza",
  "location": { 
    "lat":     40.722,
    "lon":    -73.989
  }
}

PUT /attractions/restaurant/3
{
  "name":     "Mini Munchies Pizza",
  "location": [ -73.983, 40.719 ] 
}
```



#### [地理坐标盒模型过滤器](https://www.elastic.co/guide/cn/elasticsearch/guide/current/geo-bounding-box.html)

```shell

GET /attractions/restaurant/_search
{
  "query": {
    "geo_bounding_box": {
      "location": { 
        "top_left": {
          "lat":  40.8,
          "lon": -74.0
        },
        "bottom_right": {
          "lat":  40.7,
          "lon": -73.0
        }
      }
    }
  }
}


```



#### [地理距离过滤器](https://www.elastic.co/guide/cn/elasticsearch/guide/current/geo-distance.html)

```shell
# 地理距离过滤器（ geo_distance ）以给定位置为圆心画一个圆，来找出那些地理坐标落在其中的文档

GET /attractions/restaurant/_search
{
  "query": {
    "geo_distance": {
      "distance": "1km", 
      "location": { 
        "lat":  40.715,
        "lon": -73.988
      }
    }
  }
}


# 指定不同的计算方式
GET /attractions/restaurant/_search
{
  "query": {
    "geo_distance": {
      "distance":      "1km",
      "distance_type": "plane", 
      "location": {
        "lat":  40.715,
        "lon": -73.988
      }
    }
  }
}


```



#### [按距离排序](https://www.elastic.co/guide/cn/elasticsearch/guide/current/sorting-by-distance.html)

```shell
GET /attractions/restaurant/_search
{
  "query": {
    "geo_bounding_box": {
      "type":       "indexed",
      "location": {
        "top_left": {
          "lat":  40.8,
          "lon": -74.0
        },
        "bottom_right": {
          "lat":  40.4,
          "lon": -73.0
        }
      }
    }
  },
  "sort": [
    {
      "_geo_distance": {
        "location": { 
          "lat":  40.715,
          "lon": -73.998
        },
        "order":         "asc",
        "unit":          "km", 
        "distance_type": "plane" 
      }
    }
  ]
}


# 根据距离打分
```



### [Geohashes](https://www.elastic.co/guide/cn/elasticsearch/guide/current/geohashes.html)



#### [Geohashes 映射](https://www.elastic.co/guide/cn/elasticsearch/guide/current/geohash-mapping.html)

```shell
PUT /attractions
{
  "mappings": {
    "restaurant": {
      "properties": {
        "name": {
          "type": "string"
        },
        "location": {
          "type":               "geo_point",
          "geohash_prefix":     true, 
          "geohash_precision":  "1km" 
        }
      }
    }
  }
}
```



#### [Geohash 单元查询](https://www.elastic.co/guide/cn/elasticsearch/guide/current/geohash-cell-query.html)

把经纬度坐标位置根据指定精度转换成一个 geohash ，然后查找所有包含这个 geohash 的位置——这是非常高效的查询。

```shell
GET /attractions/restaurant/_search
{
  "query": {
    "constant_score": {
      "filter": {
        "geohash_cell": {
          "location": {
            "lat":  40.718,
            "lon": -73.983
          },
          "precision": "2km" 
        }
      }
    }
  }
}



GET /attractions/restaurant/_search
{
  "query": {
    "constant_score": {
      "filter": {
        "geohash_cell": {
          "location": {
            "lat":  40.718,
            "lon": -73.983
          },
          "neighbors": true,  //寻找对应的 geohash 和包围它的 geohashes
          "precision": "2km"
        }
      }
    }
  }
}
```

### [地理位置聚合](https://www.elastic.co/guide/cn/elasticsearch/guide/current/geo-aggs.html)



#### [地理距离聚合](https://www.elastic.co/guide/cn/elasticsearch/guide/current/geo-distance-agg.html)

```shell
GET /attractions/restaurant/_search
{
  "query": {
    "bool": {
      "must": {
        "match": { 
          "name": "pizza"
        }
      },
      "filter": {
        "geo_bounding_box": {
          "location": { 
            "top_left": {
              "lat":  40.8,
              "lon": -74.1
            },
            "bottom_right": {
              "lat":  40.4,
              "lon": -73.7
            }
          }
        }
      }
    }
  },
  "aggs": {
    "per_ring": {
      "geo_distance": { 
        "field":    "location",
        "unit":     "km",
        "origin": {
          "lat":    40.712,
          "lon":   -73.988
        },
        "ranges": [
          { "from": 0, "to": 1 },
          { "from": 1, "to": 2 }
        ]
      }
    }
  },
  "post_filter": { 
    "geo_distance": {
      "distance":   "1km",
      "location": {
        "lat":      40.712,
        "lon":     -73.988
      }
    }
  }
}
```



#### [Geohash 网格聚合](https://www.elastic.co/guide/cn/elasticsearch/guide/current/geohash-grid-agg.html)

```json
GET /attractions/restaurant/_search
{
  "size" : 0,
  "query": {
    "constant_score": {
      "filter": {
        "geo_bounding_box": {
          "location": { 
            "top_left": {
              "lat":  40.8,
              "lon": -74.1
            },
            "bottom_right": {
              "lat":  40.4,
              "lon": -73.7
            }
          }
        }
      }
    }
  },
  "aggs": {
    "new_york": {
      "geohash_grid": { 
        "field":     "location",
        "precision": 5
      }
    }
  }
}
```





#### [地理边界聚合](https://www.elastic.co/guide/cn/elasticsearch/guide/current/geo-bounds-agg.html)

```shell
GET /attractions/restaurant/_search
{
  "size" : 0,
  "query": {
    "constant_score": {
      "filter": {
        "geo_bounding_box": {
          "location": {
            "top_left": {
              "lat":  40,8,
              "lon": -74.1
            },
            "bottom_right": {
              "lat":  40.4,
              "lon": -73.9
            }
          }
        }
      }
    }
  },
  "aggs": {
    "new_york": {
      "geohash_grid": {
        "field":     "location",
        "precision": 5
      }
    },
    "map_zoom": { 
      "geo_bounds": {
        "field":     "location"
      }
    }
  }
}


GET /attractions/restaurant/_search
{
  "size" : 0,
  "query": {
    "constant_score": {
      "filter": {
        "geo_bounding_box": {
          "location": {
            "top_left": {
              "lat":  40,8,
              "lon": -74.1
            },
            "bottom_right": {
              "lat":  40.4,
              "lon": -73.9
            }
          }
        }
      }
    }
  },
  "aggs": {
    "new_york": {
      "geohash_grid": {
        "field":     "location",
        "precision": 5
      },
      "aggs": {
        "cell": { 
          "geo_bounds": {
            "field": "location"
          }
        }
      }
    }
  }
}
```



#### 







### [地理形状](https://www.elastic.co/guide/cn/elasticsearch/guide/current/geo-shapes.html)

geo-shapes 有以下作用：判断查询的形状与索引的形状的关系；这些 `关系` 可能是以下之一：

- `intersects`

  查询的形状与索引的形状有重叠（默认）。

- `disjoint`

  查询的形状与索引的形状完全 *不* 重叠。

- `within`

  索引的形状完全被包含在查询的形状中。

Geo-shapes 不能用于计算距离、排序、打分以及聚合。



#### [映射地理形状](https://www.elastic.co/guide/cn/elasticsearch/guide/current/mapping-geo-shapes.html)

```json
PUT /attractions
{
  "mappings": {
    "landmark": {
      "properties": {
        "name": {
          "type": "string"
        },
        "location": {
          "type": "geo_shape"
        }
      }
    }
  }
}
```

修改两个非常重要的设置： `精度` 和 `距离误差` 。



#### [索引地理形状](https://www.elastic.co/guide/cn/elasticsearch/guide/current/indexing-geo-shapes.html)

```shell
GET /attractions/landmark/_search
{
  "query": {
    "geo_shape": {
      "location": { 
        "shape": { 
          "type":   "circle", 
          "radius": "1km",
          "coordinates": [ 
            4.89994,
            52.37815
          ]
        }
      }
    }
  }
}


GET /attractions/landmark/_search
{
  "query": {
    "geo_shape": {
      "location": {
        "relation": "within", 
        "shape": {
          "type": "polygon",
          "coordinates": [[ 
              [4.88330,52.38617],
              [4.87463,52.37254],
              [4.87875,52.36369],
              [4.88939,52.35850],
              [4.89840,52.35755],
              [4.91909,52.36217],
              [4.92656,52.36594],
              [4.93368,52.36615],
              [4.93342,52.37275],
              [4.92690,52.37632],
              [4.88330,52.38617]
            ]]
        }
      }
    }
  }
}

```



#### [在查询中使用已索引的形状](https://www.elastic.co/guide/cn/elasticsearch/guide/current/indexed-geo-shapes.html)

```json
PUT /attractions/_mapping/neighborhood
{
  "properties": {
    "name": {
      "type": "string"
    },
    "location": {
      "type": "geo_shape"
    }
  }
}


PUT /attractions/neighborhood/central_amsterdam
{
  "name" : "Central Amsterdam",
  "location" : {
      "type" : "polygon",
      "coordinates" : [[
        [4.88330,52.38617],
        [4.87463,52.37254],
        [4.87875,52.36369],
        [4.88939,52.35850],
        [4.89840,52.35755],
        [4.91909,52.36217],
        [4.92656,52.36594],
        [4.93368,52.36615],
        [4.93342,52.37275],
        [4.92690,52.37632],
        [4.88330,52.38617]
      ]]
  }
}


GET /attractions/landmark/_search
{
  "query": {
    "geo_shape": {
      "location": {
        "relation": "within",
        "indexed_shape": { 
          "index": "attractions",
          "type":  "neighborhood",
          "id":    "central_amsterdam",
          "path":  "location"
        }
      }
    }
  }
}


GET /attractions/neighborhood/_search
{
  "query": {
    "geo_shape": {
      "location": {
        "indexed_shape": {
          "index": "attractions",
          "type":  "landmark",
          "id":    "dam_square",
          "path":  "location"
        }
      }
    }
  }
}

```









## 数据建模



### [关联关系处理](https://www.elastic.co/guide/cn/elasticsearch/guide/current/relations.html)

#### [应用层联接](https://www.elastic.co/guide/cn/elasticsearch/guide/current/application-joins.html)



#### [非规范化你的数据](https://www.elastic.co/guide/cn/elasticsearch/guide/current/denormalization.html)

对每个文档保持一定数量的冗余副本可以在需要访问时避免进行关联。



#### [字段折叠](https://www.elastic.co/guide/cn/elasticsearch/guide/current/top-hits.html)

```shell
GET /my_index/blogpost/_search
{
  "size" : 0, 
  "query": { 
    "bool": {
      "must": [
        { "match": { "title":     "relationships" }},
        { "match": { "user.name": "John"          }}
      ]
    }
  },
  "aggs": {
    "users": {
      "terms": {
        "field":   "user.name.raw",      
        "order": { "top_score": "desc" } 
      },
      "aggs": {
        "top_score": { "max":      { "script":  "_score"           }}, 
        "blogposts": { "top_hits": { "_source": "title", "size": 5 }}  
      }
    }
  }
}
```



#### [非规范化和并发](https://www.elastic.co/guide/cn/elasticsearch/guide/current/denormalization-concurrency.html)

```shell
grep "some text" /clinton/projects/elasticsearch/*

PUT /fs/file/1
{
  "name":     "README.txt", 
  "path":     "/clinton/projects/elasticsearch", 
  "contents": "Starting a new Elasticsearch project is easy..."
}


grep -r "some text" /clinton

PUT /fs
{
  "settings": {
    "analysis": {
      "analyzer": {
        "paths": { 
          "tokenizer": "path_hierarchy"
        }
      }
    }
  }
}


# file 类型的映射
PUT /fs/_mapping/file
{
  "properties": {
    "name": { 
      "type":  "string",
      "index": "not_analyzed"
    },
    "path": { 
      "type":  "string",
      "index": "not_analyzed",
      "fields": {
        "tree": { 
          "type":     "string",
          "analyzer": "paths"
        }
      }
    }
  }
}

GET /fs/file/_search
{
  "query": {
    "filtered": {
      "query": {
        "match": {
          "contents": "elasticsearch"
        }
      },
      "filter": {
        "term": { 
          "path": "/clinton/projects/elasticsearch"
        }
      }
    }
  }
}

GET /fs/file/_search
{
  "query": {
    "filtered": {
      "query": {
        "match": {
          "contents": "elasticsearch"
        }
      },
      "filter": {
        "term": { 
          "path.tree": "/clinton"
        }
      }
    }
  }
}



# 重命名文件和目录
PUT /fs/file/1?version=2 
{
  "name":     "README.asciidoc",
  "path":     "/clinton/projects/elasticsearch",
  "contents": "Starting a new Elasticsearch project is easy..."
}
```



#### [解决并发问题](https://www.elastic.co/guide/cn/elasticsearch/guide/current/concurrency-solutions.html)

全局锁

```shell
# create 全局锁文档
PUT /fs/lock/global/_create
{}

# 删除全局锁文档来释放锁
DELETE /fs/lock/global

```



文档锁

```shell
PUT /fs/lock/_bulk
{ "create": { "_id": 1}} 
{ "process_id": 123    } 
{ "create": { "_id": 2}}
{ "process_id": 123    }

---
if ( ctx._source.process_id != process_id ) { 
  assert false;  
}
ctx.op = 'noop'; 

---

POST /fs/lock/1/_update
{
  "upsert": { "process_id": 123 },
  "script": "if ( ctx._source.process_id != process_id )
  { assert false }; ctx.op = 'noop';"
  "params": {
    "process_id": 123
  }
}



POST /fs/_refresh 

GET /fs/lock/_search?scroll=1m 
{
    "sort" : ["_doc"],
    "query": {
        "match" : {
            "process_id" : 123
        }
    }
}

PUT /fs/lock/_bulk
{ "delete": { "_id": 1}}
{ "delete": { "_id": 2}}
```



树锁

```shell
POST /fs/lock/%2Fclinton/_update 
{
  "upsert": { 
    "lock_type":  "shared",
    "lock_count": 1
  },
  "script": "if (ctx._source.lock_type == 'exclusive')
  { assert false }; ctx._source.lock_count++"
}
```



### [嵌套对象](https://www.elastic.co/guide/cn/elasticsearch/guide/current/nested-objects.html)

```shell
PUT /my_index/blogpost/1
{
  "title": "Nest eggs",
  "body":  "Making your money work...",
  "tags":  [ "cash", "shares" ],
  "comments": [ 
    {
      "name":    "John Smith",
      "comment": "Great article",
      "age":     28,
      "stars":   4,
      "date":    "2014-09-01"
    },
    {
      "name":    "Alice White",
      "comment": "More like this please",
      "age":     31,
      "stars":   5,
      "date":    "2014-10-22"
    }
  ]
}

GET /_search
{
  "query": {
    "bool": {
      "must": [
        { "match": { "name": "Alice" }},
        { "match": { "age":  28      }} 
      ]
    }
  }
}
```



#### [嵌套对象映射](https://www.elastic.co/guide/cn/elasticsearch/guide/current/nested-mapping.html)

```shell
PUT /my_index
{
  "mappings": {
    "blogpost": {
      "properties": {
        "comments": {
          "type": "nested", 
          "properties": {
            "name":    { "type": "string"  },
            "comment": { "type": "string"  },
            "age":     { "type": "short"   },
            "stars":   { "type": "short"   },
            "date":    { "type": "date"    }
          }
        }
      }
    }
  }
}
```



#### [嵌套对象查询](https://www.elastic.co/guide/cn/elasticsearch/guide/current/nested-query.html)

```shell
GET /my_index/blogpost/_search
{
  "query": {
    "bool": {
      "must": [
        {
          "match": {
            "title": "eggs"
          }
        },
        {
          "nested": {
            "path": "comments",
            "score_mode": "max", 
            "query": {
              "bool": {
                "must": [
                  {
                    "match": {
                      "comments.name": "john"
                    }
                  },
                  {
                    "match": {
                      "comments.age": 28
                    }
                  }
                ]
              }
            }
          }
        }
      ]
    }
  }
}
```



#### [使用嵌套字段排序](https://www.elastic.co/guide/cn/elasticsearch/guide/current/nested-sorting.html)

```shell
PUT /my_index/blogpost/2
{
  "title": "Investment secrets",
  "body":  "What they don't tell you ...",
  "tags":  [ "shares", "equities" ],
  "comments": [
    {
      "name":    "Mary Brown",
      "comment": "Lies, lies, lies",
      "age":     42,
      "stars":   1,
      "date":    "2014-10-18"
    },
    {
      "name":    "John Smith",
      "comment": "You're making it up!",
      "age":     28,
      "stars":   2,
      "date":    "2014-10-16"
    }
  ]
}


GET /_search
{
  "query": {
    "nested": { 
      "path": "comments",
      "filter": {
        "range": {
          "comments.date": {
            "gte": "2014-10-01",
            "lt":  "2014-11-01"
          }
        }
      }
    }
  },
  "sort": {
    "comments.stars": { 
      "order": "asc",   
      "mode":  "min",   
      "nested_path": "comments", 
      "nested_filter": {
        "range": {
          "comments.date": {
            "gte": "2014-10-01",
            "lt":  "2014-11-01"
          }
        }
      }
    }
  }
}
```





#### [嵌套聚合](https://www.elastic.co/guide/cn/elasticsearch/guide/current/nested-aggregation.html)

```shell
GET /my_index/blogpost/_search
{
  "size" : 0,
  "aggs": {
    "comments": { 
      "nested": {
        "path": "comments"
      },
      "aggs": {
        "by_month": {
          "date_histogram": { 
            "field":    "comments.date",
            "interval": "month",
            "format":   "yyyy-MM"
          },
          "aggs": {
            "avg_stars": {
              "avg": { 
                "field": "comments.stars"
              }
            }
          }
        }
      }
    }
  }
}

# 逆向嵌套聚合
# 基于评论者的年龄找出评论者感兴趣 tags 的分布。 
# comment.age 是一个嵌套字段，但 tags 在根文档.
GET /my_index/blogpost/_search
{
  "size" : 0,
  "aggs": {
    "comments": {
      "nested": { 
        "path": "comments"
      },
      "aggs": {
        "age_group": {
          "histogram": { 
            "field":    "comments.age",
            "interval": 10
          },
          "aggs": {
            "blogposts": {
              "reverse_nested": {}, 
              "aggs": {
                "tags": {
                  "terms": { 
                    "field": "tags"
                  }
                }
              }
            }
          }
        }
      }
    }
  }
}
```



### [父-子关系文档](https://www.elastic.co/guide/cn/elasticsearch/guide/current/parent-child.html)

#### [父-子关系文档映射](https://www.elastic.co/guide/cn/elasticsearch/guide/current/parent-child-mapping.html)

```shell
PUT /company
{
  "mappings": {
    "branch": {},
    "employee": {
      "_parent": {
        "type": "branch" 
      }
    }
  }
}
```



#### [构建父-子文档索引](https://www.elastic.co/guide/cn/elasticsearch/guide/current/indexing-parent-child.html)

父文档 ID 有两个作用：创建了父文档和子文档之间的关系，并且保证了父文档和子文档都在同一个分片上。

```shell

# 父文档并不需要知道它有哪些子文档。
POST /company/branch/_bulk
{ "index": { "_id": "london" }}
{ "name": "London Westminster", "city": "London", "country": "UK" }
{ "index": { "_id": "liverpool" }}
{ "name": "Liverpool Central", "city": "Liverpool", "country": "UK" }
{ "index": { "_id": "paris" }}
{ "name": "Champs Élysées", "city": "Paris", "country": "France" }

# 创建子文档时，用户必须要通过 parent 参数来指定该子文档的父文档 ID
PUT /company/employee/1?parent=london 
{
  "name":  "Alice Smith",
  "dob":   "1970-10-24",
  "hobby": "hiking"
}

# 父文档的 ID
POST /company/employee/_bulk
{ "index": { "_id": 2, "parent": "london" }}
{ "name": "Mark Thomas", "dob": "1982-05-16", "hobby": "diving" }
{ "index": { "_id": 3, "parent": "liverpool" }}
{ "name": "Barry Smith", "dob": "1979-04-01", "hobby": "hiking" }
{ "index": { "_id": 4, "parent": "paris" }}
{ "name": "Adrien Grand", "dob": "1987-05-11", "hobby": "horses" }

# 改变一个子文档的 parent 值要先把子文档删除，然后再重新索引这个子文档。
```



#### [通过子文档查询父文档](https://www.elastic.co/guide/cn/elasticsearch/guide/current/has-child.html)

```shell
GET /company/branch/_search
{
  "query": {
    "has_child": {
      "type": "employee",
      "query": {
        "range": {
          "dob": {
            "gte": "1980-01-01"
          }
        }
      }
    }
  }
}

GET /company/branch/_search
{
  "query": {
    "has_child": {
      "type":       "employee",
      "score_mode": "max",
      "query": {
        "match": {
          "name": "Alice Smith"
        }
      }
    }
  }
}

# 使用这两个参数时，只有当子文档数量在指定范围内时，才会返回父文档。
GET /company/branch/_search
{
  "query": {
    "has_child": {
      "type":         "employee",
      "min_children": 2, 
      "query": {
        "match_all": {}
      }
    }
  }
}


# has_child Filter
has_child 查询和过滤在运行机制上类似， 区别是 has_child 过滤不支持 score_mode 参数。has_child 过滤仅用于筛选内容--如内部的一个 filtered 查询--和其他过滤行为类似：包含或者排除，但没有进行评分。
has_child 过滤的结果没有被缓存，但是 has_child 过滤内部的过滤方法适用于通常的缓存规则。



```



#### [通过父文档查询子文档](https://www.elastic.co/guide/cn/elasticsearch/guide/current/has-parent.html)

```shell
curl -XGET "http://10.250.140.14:9200/company/employee/_search" -H 'Content-Type: application/json' -d'
{
  "query": {
    "has_parent": {
      "parent_type": "branch", 
      "query": {
        "match": {
          "country": "UK"
        }
      }
    }
  }
}'
```



`has_parent` 查询也支持 `score_mode` 这个参数，但是该参数只支持两种值： `none` （默认）和 `score` 。



#### [子文档聚合](https://www.elastic.co/guide/cn/elasticsearch/guide/current/children-agg.html)

```shell
# 按照国家维度查看最受雇员欢迎的业余爱好
GET /company/branch/_search
{
  "size" : 0,
  "aggs": {
    "country": {
      "terms": { 
        "field": "country.keyword"
      },
      "aggs": {
        "employees": {
          "children": { 
            "type": "employee"
          },
          "aggs": {
            "hobby": {
              "terms": { 
                "field": "hobby.keyword"
              }
            }
          }
        }
      }
    }
  }
}
```



#### [祖辈与孙辈关系](https://www.elastic.co/guide/cn/elasticsearch/guide/current/grandparents.html)

```shell
PUT /company
{
  "mappings": {
    "country": {},
    "branch": {
      "_parent": {
        "type": "country" 
      }
    },
    "employee": {
      "_parent": {
        "type": "branch" 
      }
    }
  }
}


POST /company/country/_bulk
{ "index": { "_id": "uk" }}
{ "name": "UK" }
{ "index": { "_id": "france" }}
{ "name": "France" }

POST /company/branch/_bulk
{ "index": { "_id": "london", "parent": "uk" }}
{ "name": "London Westmintster" }
{ "index": { "_id": "liverpool", "parent": "uk" }}
{ "name": "Liverpool Central" }
{ "index": { "_id": "paris", "parent": "france" }}
{ "name": "Champs Élysées" }


PUT /company/employee/1?parent=london
{
  "name":  "Alice Smith",
  "dob":   "1970-10-24",
  "hobby": "hiking"
}

# 添加一个额外的 routing 参数，将其设置为祖辈的文档 ID ，以此来保证三代文档路由到同一个分片上
PUT /company/employee/1?parent=london&routing=uk 
{
  "name":  "Alice Smith",
  "dob":   "1970-10-24",
  "hobby": "hiking"
}



# 找到哪些国家的雇员喜欢远足旅行，此时只需要联合 country 和 branch，以及 branch 和 employee
GET /company/country/_search
{
  "query": {
    "has_child": {
      "type": "branch",
      "query": {
        "has_child": {
          "type": "employee",
          "query": {
            "match": {
              "hobby": "hiking"
            }
          }
        }
      }
    }
  }
}




```



#### [实际使用中的一些建议](https://www.elastic.co/guide/cn/elasticsearch/guide/current/parent-child-performance.html)

```shell
PUT /company
{
  "mappings": {
    "branch": {},
    "employee": {
      "_parent": {
        "type": "branch",
        "fielddata": {
          "loading": "eager_global_ordinals" 
        }
      }
    }
  }
}
```

### [扩容设计](https://www.elastic.co/guide/cn/elasticsearch/guide/current/scale.html)

#### [扩容的单元](https://www.elastic.co/guide/cn/elasticsearch/guide/current/shard-scale.html)



[多索引](https://www.elastic.co/guide/cn/elasticsearch/guide/current/multiple-indices.html)

```shell
PUT /tweets_1/_alias/tweets_search 
PUT /tweets_1/_alias/tweets_index 


POST /_aliases
{
  "actions": [
    { "add":    { "index": "tweets_2", "alias": "tweets_search" }}, 
    { "remove": { "index": "tweets_1", "alias": "tweets_index"  }}, 
    { "add":    { "index": "tweets_2", "alias": "tweets_index"  }}  
  ]
}
```



#### [基于时间的数据](https://www.elastic.co/guide/cn/elasticsearch/guide/current/time-based.html)

```shell
POST /_aliases
{
  "actions": [
    { "add":    { "alias": "logs_current",  "index": "logs_2014-10" }}, 
    { "remove": { "alias": "logs_current",  "index": "logs_2014-09" }}, 
    { "add":    { "alias": "last_3_months", "index": "logs_2014-10" }}, 
    { "remove": { "alias": "last_3_months", "index": "logs_2014-07" }}  
  ]
}
```



#### [索引模板](https://www.elastic.co/guide/cn/elasticsearch/guide/current/index-templates.html)

```shell
PUT /_template/my_logs 
{
  "template": "logstash-*", 
  "order":    1, 
  "settings": {
    "number_of_shards": 1 
  },
  "mappings": {
    "_default_": { 
      "_all": {
        "enabled": false
      }
    }
  },
  "aliases": {
    "last_3_months": {} 
  }
}
```



#### [数据过期](https://www.elastic.co/guide/cn/elasticsearch/guide/current/retiring-data.html)



```shell
# 迁移旧数据
./bin/elasticsearch --node.box_type strong

PUT /logs_2014-10-01
{
  "settings": {
    "index.routing.allocation.include.box_type" : "strong"
  }
}


POST /logs_2014-09-30/_settings
{
  "index.routing.allocation.include.box_type" : "medium"
}

# 索引优化（Optimize）
POST /logs_2014-09-30/_settings
{ "number_of_replicas": 0 }

POST /logs_2014-09-30/_optimize?max_num_segments=1

POST /logs_2014-09-30/_settings
{ "number_of_replicas": 1 }

# 没有副本我们将面临磁盘故障而导致丢失数据的风险。你可能想要先备份数据。

# 关闭旧索引
POST /logs_2014-01-*/_flush 
POST /logs_2014-01-*/_close 
POST /logs_2014-01-*/_open
```





#### [共享索引](https://www.elastic.co/guide/cn/elasticsearch/guide/current/shared-index.html)

```shell
PUT /forums
{
  "settings": {
    "number_of_shards": 10 
  },
  "mappings": {
    "post": {
      "properties": {
        "forum_id": { 
          "type":  "string",
          "index": "not_analyzed"
        }
      }
    }
  }
}

PUT /forums/post/1
{
  "forum_id": "baking", 
  "title":    "Easy recipe for ginger nuts"
}


GET /forums/post/_search
{
  "query": {
    "bool": {
      "must": {
        "match": {
          "title": "ginger nuts"
        }
      },
      "filter": {
        "term": {
          "forum_id": {
            "baking"
          }
        }
      }
    }
  }
}


PUT /forums/post/1?routing=baking 
{
  "forum_id": "baking", 
  "title":    "Easy recipe for ginger nuts",
  ...
}

GET /forums/post/_search?routing=baking 
{
  "query": {
    "bool": {
      "must": {
        "match": {
          "title": "ginger nuts"
        }
      },
      "filter": {
        "term": { 
          "forum_id": {
            "baking"
          }
        }
      }
    }
  }
}



```



#### [利用别名实现一个用户一个索引](https://www.elastic.co/guide/cn/elasticsearch/guide/current/faking-it.html)



```shell
# 一个别名与一个索引关联起来，你可以指定一个过滤器和一个路由值
PUT /forums/_alias/baking
{
  "routing": "baking",
  "filter": {
    "term": {
      "forum_id": "baking"
    }
  }
}

# 将 baking 别名视为一个单独的索引。
# 索引至 baking 别名的文档会自动地应用我们自定义的路由值
PUT /baking/post/1 
{
  "forum_id": "baking", 
  "title":    "Easy recipe for ginger nuts",
  ...
}


# 对 baking 别名上的查询只会在自定义路由值关联的分片上运行，
# 并且结果也自动按照我们指定的过滤器进行了过滤.
GET /baking/post/_search
{
  "query": {
    "match": {
      "title": "ginger nuts"
    }
  }
}

# 当对多个论坛进行搜索时可以指定多个别名
GET /baking,recipes/post/_search 
{
  "query": {
    "match": {
      "title": "ginger nuts"
    }
  }
}
```



#### [一个大的用户](https://www.elastic.co/guide/cn/elasticsearch/guide/current/one-big-user.html)

```shell
# 那个论坛创建一个新的索引，并为其分配合理的分片数，可以满足一定预期的数据增长
PUT /baking_v1
{
  "settings": {
    "number_of_shards": 3
  }
}

# 将共享的索引中的数据迁移到专用的索引中，可以通过scroll查询和bulk API来实现。 
# 当迁移完成时，可以更新索引别名指向那个新的索引
POST /_aliases
{
  "actions": [
    { "remove": { "alias": "baking", "index": "forums"    }},
    { "add":    { "alias": "baking", "index": "baking_v1" }}
  ]
}


```



#### [扩容并不是无限的](https://www.elastic.co/guide/cn/elasticsearch/guide/current/finite-scale.html)

```shell
GET /_cluster/state
```







## [管理、监控和部署](https://www.elastic.co/guide/cn/elasticsearch/guide/current/administration.html)

### [监控](https://www.elastic.co/guide/cn/elasticsearch/guide/current/cluster-admin.html)

#### [Marvel 监控](https://www.elastic.co/guide/cn/elasticsearch/guide/current/marvel.html)

#### [集群健康](https://www.elastic.co/guide/cn/elasticsearch/guide/current/_cluster_health.html) 

```shell
curl -XGET "http://10.250.140.14:9200/_cluster/health"

# 集群信息里添加一个索引清单
curl -XGET "http://10.250.140.14:9200/_cluster/health?level=indices"

# shards 选项会提供一个详细得多的输出，列出每个索引里每个分片的状态和位置。
curl -XGET "http://10.250.140.14:9200/_cluster/health?level=shards"
# 阻塞等待状态变化编辑
curl -XGET "http://10.250.140.14:9200/_cluster/health?wait_for_status=yellow"
```



#### [监控单个节点](https://www.elastic.co/guide/cn/elasticsearch/guide/current/_monitoring_individual_nodes.html)

```shell
# 节点统计值
curl -XGET "http://10.250.140.14:9200/_nodes/stats"

```



**索引(indices)部分**

```json
"indices": {
	"docs": {
	  "count": 589845,  //节点内存有多少文档
	  "deleted": 19447  //还没有从段里清除的已删除文档数量
	},
	"store": {
	  "size_in_bytes": 404392698, 	//节点耗用了多少物理存储。这个指标包括主分片和副本分片在内
	  "throttle_time_in_millis": 0 	//限流时间很大，那可能表明你的磁盘限流设置得过低
	},
	"indexing": { 	//已经索引了多少文档,这个值是一个累加计数器。在文档被删除的时候，数值不会下降。
					//还要注意的是，在发生内部 索引 操作的时候，这个值也会增加，比如说文档更新。
	  "index_total": 2360602,
	  "index_time_in_millis": 440766, //索引操作耗费的时间
	  "index_current": 0,  //正在索引的文档数量
	  "index_failed": 0,
	  "delete_total": 5336, //删除操作
	  "delete_time_in_millis": 326,
	  "delete_current": 0,
	  "noop_update_total": 0,
	  "is_throttled": false,
	  "throttle_time_in_millis": 0
	},
	"get": { //通过 ID 获取文档的接口相关的统计值。包括对单个文档的 GET 和 HEAD 请求。
	  "total": 37217,
	  "time_in_millis": 2498,
	  "exists_total": 37118,
	  "exists_time_in_millis": 2495,
	  "missing_total": 99,
	  "missing_time_in_millis": 3,
	  "current": 0
	},
	"search": { //在活跃中的搜索（ open_contexts ）数量、查询的总数量、
				//以及自节点启动以来在查询上消耗的总时间。用 query_time_in_millis / query_total 计算的比值，
				//可以用来粗略的评价你的查询有多高效。比值越大，每个查询花费的时间越多
	  "open_contexts": 0,
	  "query_total": 75678,
	  "query_time_in_millis": 8489,
	  "query_current": 0,
	  "fetch_total": 70097, //查询处理的后一半流程（query-then-fetch 里的 fetch ）。
							//如果 fetch 耗时比 query 还多，说明磁盘较慢，或者获取了太多文档，
							//或者可能搜索请求设置了太大的分页（比如， size: 10000 ）。
	  "fetch_time_in_millis": 2571,
	  "fetch_current": 0,
	  "scroll_total": 7726,
	  "scroll_time_in_millis": 2604,
	  "scroll_current": 0,
	  "suggest_total": 0,
	  "suggest_time_in_millis": 0,
	  "suggest_current": 0
	},
	"merges": { //Lucene 段合并相关的信息。它会告诉你目前在运行几个合并，合并涉及的文档数量，
				//正在合并的段的总大小，以及在合并操作上消耗的总时间。
				//在你的集群写入压力很大时，合并统计值非常重要。
				//合并要消耗大量的磁盘 I/O 和 CPU 资源。
				//如果你的索引有大量的写入，同时又发现大量的合并数.
				//注意：文档更新和删除也会导致大量的合并数，因为它们会产生最终需要被合并的段 碎片 。
	  "current": 0,
	  "current_docs": 0,
	  "current_size_in_bytes": 0,
	  "total": 2115,
	  "total_time_in_millis": 836818,
	  "total_docs": 10260577,
	  "total_size_in_bytes": 5951942556,
	  "total_stopped_time_in_millis": 0,
	  "total_throttled_time_in_millis": 415,
	  "total_auto_throttle_in_bytes": 2680541556
	},
	"refresh": {
	  "total": 22778,
	  "total_time_in_millis": 360677,
	  "listeners": 0
	},
	"flush": {
	  "total": 124,
	  "total_time_in_millis": 746
	},
	"warmer": {
	  "current": 0,
	  "total": 22908,
	  "total_time_in_millis": 6705
	},
	"query_cache": {
	  "memory_size_in_bytes": 130079,
	  "total_count": 18897,
	  "hit_count": 17545,
	  "miss_count": 1352,
	  "cache_size": 40,
	  "cache_count": 419,
	  "evictions": 379
	},
	"fielddata": {
	  "memory_size_in_bytes": 1416,
	  "evictions": 0
	},
	"completion": {
	  "size_in_bytes": 0
	},
	"segments": { 	//这个节点目前正在服务中的 Lucene 段的数量。
					//这是一个重要的数字。大多数索引会有大概 50–150 个段，
					//哪怕它们存有 TB 级别的数十亿条文档。
					//段数量过大表明合并出现了问题（比如，合并速度跟不上段的创建）。
					//注意这个统计值是节点上所有索引的汇聚总数。
	  "count": 152,
	  "memory_in_bytes": 2588220,
	  "terms_memory_in_bytes": 1219255,
	  "stored_fields_memory_in_bytes": 100192,
	  "term_vectors_memory_in_bytes": 0,
	  "norms_memory_in_bytes": 37504,
	  "points_memory_in_bytes": 147925,
	  "doc_values_memory_in_bytes": 1083344,
	  "index_writer_memory_in_bytes": 13119602,
	  "version_map_memory_in_bytes": 1296298,
	  "fixed_bit_set_memory_in_bytes": 2288,
	  "max_unsafe_auto_id_timestamp": -1,
	  "file_sizes": {}
	},
	"translog": {
	  "operations": 726339,
	  "size_in_bytes": 467955330
	},
	"request_cache": {
	  "memory_size_in_bytes": 4258,
	  "evictions": 0,
	  "hit_count": 23545,
	  "miss_count": 14
	},
	"recovery": {
	  "current_as_source": 0,
	  "current_as_target": 0,
	  "throttle_time_in_millis": 0
	}
}
```



**JVM部分**

```shell
"jvm": {
	"timestamp": 1523601257737,
	"uptime_in_millis": 80099642,
	"mem": {
	  "heap_used_in_bytes": 667677744, //有多少 heap 被使用了
	  "heap_used_percent": 32, 	//Elasticsearch 被配置为当 heap 达到 75% 的时候开始 GC。
								//如果你的节点一直 >= 75%，你的节点正处于 内存压力 状态。
								//这是个危险信号，不远的未来可能就有慢 GC 要出现了。
								//如果 heap 使用率一直 >=85%，你就麻烦了。Heap 在 90–95% 之间，
								//则面临可怕的性能风险，此时最好的情况是长达 10–30s 的 GC，
								//最差的情况就是内存溢出（OOM）异常。
	  "heap_committed_in_bytes": 2077753344,  //多少被指派了（当前被分配给进程的）
	  "heap_max_in_bytes": 2077753344, //heap 被允许分配的最大值
	  "non_heap_used_in_bytes": 158566152,
	  "non_heap_committed_in_bytes": 167346176,
	  "pools": {
		"young": {
		  "used_in_bytes": 73469112,
		  "max_in_bytes": 558432256,
		  "peak_used_in_bytes": 558432256,
		  "peak_max_in_bytes": 558432256
		},
		"survivor": {
		  "used_in_bytes": 11587168,
		  "max_in_bytes": 69730304,
		  "peak_used_in_bytes": 69730304,
		  "peak_max_in_bytes": 69730304
		},
		"old": {
		  "used_in_bytes": 582621464,
		  "max_in_bytes": 1449590784,
		  "peak_used_in_bytes": 582621464,
		  "peak_max_in_bytes": 1449590784
		}
	  }
	},
	"threads": {
	  "count": 128,
	  "peak_count": 131
	},
	"gc": { //gc 部分显示新生代和老生代的垃圾回收次数和累积时间。
			//大多数时候你可以忽略掉新生代的次数：这个数字通常都很大。这是正常的。
			//老生代的次数应该很小，而且 collection_time_in_millis 也应该很小。
			//这些是累积值，所以很难给出一个阈值表示你要开始操心了
	  "collectors": {
		"young": {
		  "collection_count": 1648,
		  "collection_time_in_millis": 18468
		},
		"old": {
		  "collection_count": 1,
		  "collection_time_in_millis": 58
		}
	  }
	},
	"buffer_pools": {
	  "direct": {
		"count": 81,
		"used_in_bytes": 269739275,
		"total_capacity_in_bytes": 269739274
	  },
	  "mapped": {
		"count": 266,
		"used_in_bytes": 402695060,
		"total_capacity_in_bytes": 402695060
	  }
	},
	"classes": {
	  "current_loaded_count": 14792,
	  "total_loaded_count": 14792,
	  "total_unloaded_count": 0
	}
}
```



**线程池部分**

```shell
"thread_pool": {
	"bulk": { //批量请求，和单条的索引请求不同的线程池
	  "threads": 8, //已配置的线程数量（ threads ），
	  "queue": 0, 	//在队列中等待处理的任务单元数量（ queue ）。
	  "active": 0, 	//当前在处理任务的线程数量（ active ），
	  "rejected": 0,	//如果队列中任务单元数达到了极限，新的任务单元会开始被拒绝，
						//你会在 rejected 统计值上看到它反映出来。这通常是你的集群在某些资源上碰到瓶颈的信号。
						//因为队列满意味着你的节点或集群在用最高速度运行，但依然跟不上工作的蜂拥而入。
	  "largest": 8,
	  "completed": 50607
	},
	"fetch_shard_started": {
	  "threads": 1,
	  "queue": 0,
	  "active": 0,
	  "rejected": 0,
	  "largest": 16,
	  "completed": 124
	},
	"fetch_shard_store": {
	  "threads": 0,
	  "queue": 0,
	  "active": 0,
	  "rejected": 0,
	  "largest": 0,
	  "completed": 0
	},
	"flush": {
	  "threads": 1,
	  "queue": 0,
	  "active": 0,
	  "rejected": 0,
	  "largest": 4,
	  "completed": 247
	},
	"force_merge": {
	  "threads": 0,
	  "queue": 0,
	  "active": 0,
	  "rejected": 0,
	  "largest": 0,
	  "completed": 0
	},
	"generic": {
	  "threads": 5,
	  "queue": 0,
	  "active": 0,
	  "rejected": 0,
	  "largest": 5,
	  "completed": 104242
	},
	"get": { //Get-by-ID 操作
	  "threads": 8,
	  "queue": 0,
	  "active": 0,
	  "rejected": 0,
	  "largest": 8,
	  "completed": 31881
	},
	"index": { //普通的索引请求的线程池
	  "threads": 8,
	  "queue": 0,
	  "active": 0,
	  "rejected": 0,
	  "largest": 8,
	  "completed": 5336
	},
	"listener": {
	  "threads": 0,
	  "queue": 0,
	  "active": 0,
	  "rejected": 0,
	  "largest": 0,
	  "completed": 0
	},
	"management": {
	  "threads": 4,
	  "queue": 0,
	  "active": 1,
	  "rejected": 0,
	  "largest": 4,
	  "completed": 201432
	},
	"ml_autodetect": {
	  "threads": 0,
	  "queue": 0,
	  "active": 0,
	  "rejected": 0,
	  "largest": 0,
	  "completed": 0
	},
	"ml_datafeed": {
	  "threads": 0,
	  "queue": 0,
	  "active": 0,
	  "rejected": 0,
	  "largest": 0,
	  "completed": 0
	},
	"ml_utility": {
	  "threads": 1,
	  "queue": 0,
	  "active": 0,
	  "rejected": 0,
	  "largest": 1,
	  "completed": 1
	},
	"refresh": {
	  "threads": 4,
	  "queue": 0,
	  "active": 0,
	  "rejected": 0,
	  "largest": 4,
	  "completed": 3110228
	},
	"search": { //所有的搜索和查询请求
	  "threads": 13,
	  "queue": 0,
	  "active": 0,
	  "rejected": 0,
	  "largest": 13,
	  "completed": 148834
	},
	"security-token-key": {
	  "threads": 0,
	  "queue": 0,
	  "active": 0,
	  "rejected": 0,
	  "largest": 0,
	  "completed": 0
	},
	"snapshot": {
	  "threads": 0,
	  "queue": 0,
	  "active": 0,
	  "rejected": 0,
	  "largest": 0,
	  "completed": 0
	},
	"warmer": {
	  "threads": 4,
	  "queue": 0,
	  "active": 0,
	  "rejected": 0,
	  "largest": 4,
	  "completed": 32843
	},
	"watcher": {
	  "threads": 40,
	  "queue": 0,
	  "active": 0,
	  "rejected": 0,
	  "largest": 40,
	  "completed": 5336
	}
}

```

**文件系统和网络部分**

```shell
"fs": {
	"timestamp": 1523601257738,
	"total": {
	  "total_in_bytes": 211242950656,
	  "free_in_bytes": 148575768576,
	  "available_in_bytes": 137821626368,
	  "spins": "true"
	},
	"data": [
	  {
		"path": "/home/lgd/install/elasticsearch-5.5.2/data/nodes/0",
		"mount": "/home (/dev/vdb1)",
		"type": "ext4",
		"total_in_bytes": 211242950656,
		"free_in_bytes": 148575768576,
		"available_in_bytes": 137821626368,
		"spins": "true"
	  }
	],
	"io_stats": {
	  "devices": [
		{
		  "device_name": "vdb1",
		  "operations": 377770,
		  "read_operations": 16,
		  "write_operations": 377754,
		  "read_kilobytes": 104,
		  "write_kilobytes": 11102676
		}
	  ],
	  "total": {
		"operations": 377770,
		"read_operations": 16,
		"write_operations": 377754,
		"read_kilobytes": 104,
		"write_kilobytes": 11102676
	  }
	}
},
"transport": {	//transport 显示和 传输地址 相关的一些基础统计值。
				//包括节点间的通信（通常是 9300 端口）以及任意传输客户端或者节点客户端的连接。
				//如果看到这里有很多连接数不要担心；Elasticsearch 在节点之间维护了大量的连接。
	"server_open": 0,
	"rx_count": 0,
	"rx_size_in_bytes": 0,
	"tx_count": 0,
	"tx_size_in_bytes": 0
},
"http": { 	//显示 HTTP 端口（通常是 9200）的统计值。如果你看到 total_opened 数很大而且还在一直上涨，
			//这是一个明确信号，说明你的 HTTP 客户端里有没启用 keep-alive 长连接的。
			//持续的 keep-alive 长连接对性能很重要，因为连接、
			//断开套接字是很昂贵的（而且浪费文件描述符）。请确认你的客户端都配置正确。
	"current_open": 13,
	"total_opened": 2838
}
```

**断路器**

```json
"breakers": {
	"request": {
	  "limit_size_in_bytes": 1246652006,
	  "limit_size": "1.1gb",
	  "estimated_size_in_bytes": 0,
	  "estimated_size": "0b",
	  "overhead": 1,
	  "tripped": 0
	},
	"fielddata": {
	  "limit_size_in_bytes": 1246652006, //断路器的最大值
	  "limit_size": "1.1gb",
	  "estimated_size_in_bytes": 1416,
	  "estimated_size": "1.3kb",
	  "overhead": 1.03,
	  "tripped": 0 		//如果这个数字很大或者持续上涨，这是一个信号，
						//说明你的请求需要优化，或者你需要添加更多内存
						//（单机上添加，或者通过添加新节点的方式）
	},
	"in_flight_requests": {
	  "limit_size_in_bytes": 2077753344,
	  "limit_size": "1.9gb",
	  "estimated_size_in_bytes": 0,
	  "estimated_size": "0b",
	  "overhead": 1,
	  "tripped": 0
	},
	"parent": {
	  "limit_size_in_bytes": 1454427340,
	  "limit_size": "1.3gb",
	  "estimated_size_in_bytes": 1416,
	  "estimated_size": "1.3kb",
	  "overhead": 1,
	  "tripped": 0
	}
}
```



#### [集群统计](https://www.elastic.co/guide/cn/elasticsearch/guide/current/_cluster_stats.html)

```shell
curl -XGET "http://10.250.140.14:9200/_cluster/stats"
```



#### [索引统计](https://www.elastic.co/guide/cn/elasticsearch/guide/current/_index_stats.html)

```shell
# 统计 my_index 索引
curl -XGET "http://10.250.140.14:9200/my_index/_stats"

# 使用逗号分隔索引名可以请求多个索引统计值。
curl -XGET "http://10.250.140.14:9200/my_index,my_index_v2/_stats"

# 使用特定的 _all 可以请求全部索引的统计值
curl -XGET "http://10.250.140.14:9200/_all/_stats"
```





#### [等待中的任务](https://www.elastic.co/guide/cn/elasticsearch/guide/current/_pending_tasks.html)

```shell
curl -XGET "http://10.250.140.14:9200/_cluster/pending_tasks"
```



#### [cat API](https://www.elastic.co/guide/cn/elasticsearch/guide/current/_cat_api.html)

```shell
curl -XGET "http://10.250.140.14:9200/_cat"

# 健康检查 
curl -XGET "http://10.250.140.14:9200/_cat/health"

# 启动表头
curl -XGET "http://10.250.140.14:9200/_cat/health?v"

# 节点统计
curl -XGET "http://10.250.140.14:9200/_cat/nodes?v"

# 对任意 API 添加 ?help 参数来做到这点
curl -XGET "http://10.250.140.14:9200/_cat/nodes?help"

# 用 ?h 参数来明确指定显示这些指标
curl -XGET "http://10.250.140.14:9200/_cat/nodes?v&h=ip,port,heapPercent,heapMax"

# 因为 cat API 试图像 *nix 工具一样工作，你可以使用管道命令将结果传递给其他工具，
# 比如 sort 、 grep 或者 awk 。
curl -XGET "http://10.250.140.14:9200/_cat/indices?bytes=b"


curl -XGET -u liguodong:liguodong "http://10.250.140.14:9200/_cat/indices?bytes=b" | sort -rnk8

# 排除marvel索引
curl -XGET -u liguodong:liguodong "http://10.250.140.14:9200/_cat/indices?bytes=b" | sort -rnk8| grep -v marvel
```



###[部署](https://www.elastic.co/guide/cn/elasticsearch/guide/current/deploy.html)

#### [硬件](https://www.elastic.co/guide/cn/elasticsearch/guide/current/hardware.html)



内存

64 GB 内存的机器是非常理想的， 但是32 GB 和16 GB 机器也是很常见的。少于8 GB 会适得其反（你最终需要很多很多的小机器），大于64 GB 的机器也会有问题。



CPUs

常见的集群使用两到八个核的机器。

多个内核提供的额外并发远胜过稍微快一点点的时钟频率。



硬盘

 基于 SSD 的节点，查询和索引性能都有提升。如果你负担得起，SSD 是一个好的选择。

网络

快速可靠的网络显然对分布式系统的性能是很重要的 。 低延时能帮助确保节点间能容易的通讯，大带宽能帮助分片移动和恢复。现代数据中心网络（1 GbE, 10 GbE）对绝大多数集群都是足够的。

即使数据中心们近在咫尺，也要避免集群跨越多个数据中心。绝对要避免集群跨越大的地理距离。



#### [Java 虚拟机](https://www.elastic.co/guide/cn/elasticsearch/guide/current/_java_virtual_machine.html)





#### [Transport Client 与 Node Client](https://www.elastic.co/guide/cn/elasticsearch/guide/current/_transport_client_versus_node_client.html)

#### [配置管理](https://www.elastic.co/guide/cn/elasticsearch/guide/current/_configuration_management.html)

配置管理工具（ Puppet，Chef，Ansible）



#### [重要配置的修改](https://www.elastic.co/guide/cn/elasticsearch/guide/current/important-configuration-changes.html)

```shell
curl -XPUT "http://10.250.140.14:9200/_cluster/settings" -H 'Content-Type: application/json' -d'
{
    "persistent" : {
        "discovery.zen.minimum_master_nodes" : 2
    }
}'


gateway.recover_after_nodes: 8
gateway.expected_nodes: 10
gateway.recover_after_time: 5m
Elasticsearch 会采取如下操作：
等待集群至少存在 8 个节点
等待 5 分钟，或者10 个节点上线后，才进行数据恢复，这取决于哪个条件先达到。

注意：这些配置只能设置在 config/elasticsearch.yml 文件中或者是在命令行里（它们不能动态更新）它们只在整个集群重启的时候有实质性作用。


你的单播列表不需要包含你的集群中的所有节点， 它只是需要足够的节点，当一个新节点联系上其中一个并且说上话就可以了。如果你使用 master 候选节点作为单播列表，你只要列出三个就可以了。 这个配置在 elasticsearch.yml 文件中：
discovery.zen.ping.unicast.hosts: ["host1", "host2:port"]
```



#### [不要触碰这些配置！](https://www.elastic.co/guide/cn/elasticsearch/guide/current/dont-touch-these-settings.html)

垃圾收集器

线程池



#### [堆内存:大小和交换](https://www.elastic.co/guide/cn/elasticsearch/guide/current/heap-sizing.html)

```shell
export ES_HEAP_SIZE=10g
./bin/elasticsearch -Xmx10g -Xms10g 
```





### [文件描述符和 MMap](https://www.elastic.co/guide/cn/elasticsearch/guide/current/_file_descriptors_and_mmap.html)

```shell
curl -XGET "http://10.250.150.243:9292/_nodes/process"


```













### [部署后](https://www.elastic.co/guide/cn/elasticsearch/guide/current/post_deploy.html)



#### [动态变更设置](https://www.elastic.co/guide/cn/elasticsearch/guide/current/_changing_settings_dynamically.html)

```
PUT /_cluster/settings
{
    "persistent" : {
        "discovery.zen.minimum_master_nodes" : 2 
    },
    "transient" : {
        "indices.store.throttle.max_bytes_per_sec" : "50mb" 
    }
}
```



#### [日志记录](https://www.elastic.co/guide/cn/elasticsearch/guide/current/logging.html)

```shell
PUT /_cluster/settings
{
    "transient" : {
        "logger.discovery" : "DEBUG"
    }
}

PUT /my_index/_settings
{
    "index.search.slowlog.threshold.query.warn" : "10s", 
    "index.search.slowlog.threshold.fetch.debug": "500ms", 
    "index.indexing.slowlog.threshold.index.info": "5s" 
}

PUT /_cluster/settings
{
    "transient" : {
        "logger.index.search.slowlog" : "DEBUG", 
        "logger.index.indexing.slowlog" : "WARN" 
    }
}
```





#### [索引性能技巧](https://www.elastic.co/guide/cn/elasticsearch/guide/current/indexing-performance.html)

段合并

```shell
# 限流阈值
PUT /_cluster/settings
{
    "persistent" : {
        "indices.store.throttle.max_bytes_per_sec" : "100mb"
    }
}

# 彻底关掉合并限流，等你完成了导入，记得改回 merge 重新打开限流
PUT /_cluster/settings
{
    "transient" : {
        "indices.store.throttle.type" : "none" 
    }
}
```



**elasticsearch.yml**

```
index.merge.scheduler.max_thread_count: 1
```

#### [推迟分片分配](https://www.elastic.co/guide/cn/elasticsearch/guide/current/_delaying_shard_allocation.html)

```shell
PUT /_cluster/settings
{
    "transient" : {
        "cluster.routing.allocation.enable" : "none"
    }
}


PUT /_cluster/settings
{
    "transient" : {
        "cluster.routing.allocation.enable" : "all"
    }
}
```



#### [滚动重启](https://www.elastic.co/guide/cn/elasticsearch/guide/current/_rolling_restarts.html)



```shell
# 禁止分片分配
PUT /_cluster/settings
{
    "transient" : {
        "cluster.routing.allocation.enable" : "none"
    }
}

# 重启分片分配
PUT /_cluster/settings
{
    "transient" : {
        "cluster.routing.allocation.enable" : "all"
    }
}
```



#### [备份你的集群](https://www.elastic.co/guide/cn/elasticsearch/guide/current/backing-up-your-cluster.html)

```shell
# 创建仓库
PUT _snapshot/my_backup 
{
    "type": "fs", 
    "settings": {
        "location": "/mount/backups/my_backup" 
    }
}

POST _snapshot/my_backup/ 
{
    "type": "fs",
    "settings": {
        "location": "/mount/backups/my_backup",
        "max_snapshot_bytes_per_sec" : "50mb", 
        "max_restore_bytes_per_sec" : "50mb"
    }
}

# 备份所有打开的索引
PUT _snapshot/my_backup/snapshot_1

# 删除快照
DELETE _snapshot/my_backup/snapshot_2

# 监控快照进度
GET _snapshot/my_backup/snapshot_3
GET _snapshot/my_backup/snapshot_3/_status

# 取消一个快照
DELETE _snapshot/my_backup/snapshot_3
```



#### [从快照恢复](https://www.elastic.co/guide/cn/elasticsearch/guide/current/_restoring_from_a_snapshot.html)

```shell
POST _snapshot/my_backup/snapshot_1/_restore

# 指定恢复的快照
POST /_snapshot/my_backup/snapshot_1/_restore
{
    "indices": "index_1", 
    "rename_pattern": "index_(.+)", 
    "rename_replacement": "restored_index_$1" 
}

# 如果你更希望你的 HTTP 调用阻塞直到恢复完成，添加 wait_for_completion 标记
POST _snapshot/my_backup/snapshot_1/_restore?wait_for_completion=true
```







```shell
# 恢复的指定索引
GET restored_index_3/_recovery

# 查看你集群里所有索引，可能包括跟你的恢复进程无关的其他分片移动
GET /_recovery/

# 取消一个恢复
DELETE /restored_index_3
```







**Elasticsearch: 权威指南：**<https://www.elastic.co/guide/cn/elasticsearch/guide/current/index.html>





































































**Elasticsearch: 权威指南：**<https://www.elastic.co/guide/cn/elasticsearch/guide/current/index.html>