# Test

要按照顺序测~

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

owner is "*" (不确定能不能在client测)

	java -jar client.jar -host 10.13.44.164 -port 20006 -publish -uri http://www.dubai.com -owner * -debug

URI not official

	java -jar client.jar -host 10.13.44.164 -port 20006 -publish -uri www.melb.com -debug
	java -jar client.jar -host 10.13.44.164 -port 20006 -publish -uri abc -debug
	java -jar client.jar -host 10.13.44.164 -port 20006 -publish -uri 分布式系统 -debug

URI not absolute

	java -jar client.jar -host 10.13.44.164 -port 20006 -publish -uri //www.internet.com/ -debug
	java -jar client.jar -host 10.13.44.164 -port 20006 -publish -uri about.html -debug

### error: missing resource (直接发JSON)

## SHARE

### error: invalid command (直接发JSON)
### error: missing or incorrect type for command (直接发JSON)
### success
### error: cannot share resource
### error: incorrect secret
### error: missing resource and\/or secret

## REMOVE

### error: invalid command (直接发JSON)
### error: missing or incorrect type for command (直接发JSON)
### success
### error: cannot remove resource
### error: invalid resource
### error: missing resource

## QUERY

### error: invalid command (直接发JSON)
### error: missing or incorrect type for command (直接发JSON)
### success [RESOURCE] resultSize:*
### error: invalid resourceTemplate
### error: missing resourceTemplate

## FETCH

### error: invalid command (直接发JSON)
### error: missing or incorrect type for command (直接发JSON)
### success [RESOURCE] resultSize:1
### error: invalid resourceTemplate
### error: missing resourceTemplate

## EXCHANGE

### error: invalid command (直接发JSON)
### error: missing or incorrect type for command (直接发JSON)
### success
### error: missing resourceTemplate
### error: missing or invalid server list