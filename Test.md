## PUBLISH

#### error: invalid command
#### error: missing or incorrect type for command
#### success
  java -jar client.jar -host 10.13.44.164 -port 20006 -publish -name website -uri http://www.baidu.com -debug
  java -jar client.jar -host 10.13.44.164 -port 20006 -publish -name bilibili -uri http://www.bilibili.com -channel blbl -debug
  
Overwrite:

`java -jar client.jar -host 10.13.44.164 -port 20006 -publish -name bilibili -uri http://www.bilibili.com -channel blbl -description good website -debug`

4. error: cannot publish resource

The resource is a file:

`java -jar client.jar -host 10.13.44.164 -port 20006 -publish -uri file://users/aaaa.java -debug`

Same channel, same URI, different owner:

`java -jar client.jar -host 10.13.44.164 -port 20006 -publish -name bilibili -uri http://www.bilibili.com -channel blbl -description good website -owner bilibili -debug`

URI missing:


5. error: invalid resource
6. error: missing resource

# SHARE #

1. error: invalid command
2. error: missing or incorrect type for command
3. success
4. error: cannot share resource
5. error: incorrect secret
6. error: missing resource and\/or secret

# REMOVE #

1. error: invalid command
2. error: missing or incorrect type for command
3. success
4. error: cannot remove resource
5. error: invalid resource
6. error: missing resource

# QUERY #

1. error: invalid command
2. error: missing or incorrect type for command
3. success [RESOURCE] resultSize:*
4. error: invalid resourceTemplate
5. error: missing resourceTemplate

# FETCH #

1. error: invalid command
2. error: missing or incorrect type for command
3. success [RESOURCE] resultSize:1
4. error: invalid resourceTemplate
5. error: missing resourceTemplate

# EXCHANGE #

1. error: invalid command
2. error: missing or incorrect type for command
3. success
4. error: missing resourceTemplate
5. error: missing or invalid server list
