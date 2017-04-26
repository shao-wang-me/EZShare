# Test

	sunrise.cis.unimelb.edu.au:3780

## PUBLISH

### error: invalid command (直接发JSON)
### error: missing or incorrect type for command (直接发JSON)
### success

	java -jar client.jar -host 10.13.44.164 -port 20006 -publish -name "baidu" -uri http://www.baidu.com -debug
	java -jar client.jar -host 10.13.44.164 -port 20006 -publish -name "bilibili" -uri http://www.bilibili.com -channel carton -debug
	java -jar client.jar -host 10.13.44.164 -port 20006 -publish -uri http://daringfireball.net/projects/markdown/syntax -channel markdown -owner DF -descrition "Daring Fireball: Markdown Syntax Documentation" -debug
  
overwrite

	java -jar client.jar -host 10.13.44.164 -port 20006 -publish -name "bilibili" -uri http://www.bilibili.com -channel carton -description "bilibili" -debug

### error: cannot publish resource

resource is a file

	java -jar client.jar -host 10.13.44.164 -port 20006 -publish -uri file://users/abc.java -debug

same channel, same URI, different owner

	java -jar client.jar -host 10.13.44.164 -port 20006 -publish -name "bilibili" -uri http://www.bilibili.com -channel carton -description "BILIBILI" -owner bilibili -debug
	java -jar client.jar -host 10.13.44.164 -port 20006 -publish -uri http://daringfireball.net/projects/markdown/syntax -channel markdown -owner github -descrition "Daring Fireball: Markdown Syntax Documentation" -debug

URI missing (不确定能不能在client测)

	java -jar client.jar -host 10.13.44.164 -port 20006 -publish -name "bilibili" -debug
	java -jar client.jar -host 10.13.44.164 -port 20006 -publish -name "bilibili" -debug

### error: invalid resource

URI is empty (不确定能不能在client测)

	java -jar client.jar -host 10.13.44.164 -port 20006 -publish -name "bilibili" -uri -debug

owner is "*"

	java -jar client.jar -host 10.13.44.164 -port 20006 -publish -uri http://www.dubai.com -owner * -debug

URI not official

	java -jar client.jar -host 10.13.44.164 -port 20006 -publish -uri www.melb.com
	java -jar client.jar -host 10.13.44.164 -port 20006 -publish -uri abc
	java -jar client.jar -host 10.13.44.164 -port 20006 -publish -uri 分布式系统

URI not absolute

	java -jar client.jar -host 10.13.44.164 -port 20006 -publish -uri //www.internet.com/
	java -jar client.jar -host 10.13.44.164 -port 20006 -publish -uri about.html

### error: missing resource (直接发JSON)

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
