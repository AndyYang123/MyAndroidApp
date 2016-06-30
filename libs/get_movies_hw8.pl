#!/usr/bin/perl -w
use utf8;

print "Content-type:application/xml","\n\n";
# Add a header directive indicating that this is encoded in UTF-8
print "<?xml version=\"1.0\" encoding=\"UTF-8\"?>";

# Check whether LWP module is installed
if(eval{require LWP::Simple;}){
}else{
print "You need to install the Perl LWP module!<br>";
exit;
}

# Retrieve the content of an URL
$buffer = $ENV{'QUERY_STRING'};
@pairs = split(/&/, $buffer);
foreach $pair (@pairs) 
{
	($name, $value) = split(/=/, $pair);
	if($name =~ /title/)  {$title = $value;}
	if($name =~ /type/)  {$type = $value;}
}
$url = "http://www.imdb.com/search/title?title=$title&title_type=$type";

$content = LWP::Simple::get($url);

if($content =~ /No results./)   
	{	print "<movies total='0'></movies>";}
else
{
print "<movies ";
#$tmp = $content;
$tmp = LWP::Simple::get($url);
$count1=0;
while($tmp =~ m/detailed/)
{
	$movie_index = index($tmp, "detailed");
	$tmp = substr($tmp, $movie_index+25);
	$count1++;
}
if($count1>4) 
{$count1=5;}
print "total=\"$count1\">\n";

$count=0;
while($count<5)     #content =~ m/detailed/
	{
		$movie_index = index($content, "detailed");
		$content = substr($content, $movie_index+5);
		$movie_index2 = index($content, "detailed");
		if($movie_index2 == -1) {$count=5;}
		$content_temp = substr($content, 0, $movie_index2);
		
		if($content_temp =~ m/a href=\"(.*)\" title=\"(.*) \((.*)\)\"><img src=\"(.*)\" height/)
		{	
			print "<movie image=\"$4\" title=\"$2\" year=\"$3\" ";
		}
		
		$i=0;
		while($i<3)
		{
			if($content_temp =~ m/Dir: <a href=\"(.*)\">(.*)<\/a>, <a href=\"(.*)\">(.*)<\/a>/)
			{
				print "director=\"$2,$4\" ";last;
			}
			if($content_temp =~ m/Dir: <a href=\"(.*)\">(.*)<\/a>/)
			{
				print "director=\"$2\" ";last;
			}
			if($content_temp !~ m/Dir: <a href=\"(.*)\">(.*)<\/a/)
			{
				print "director=\"N.A.\" ";last;
			}
		}
		
		$j=0;
		while($j<3)
		{
			if($content_temp =~ m/title=\"Users rated this (.*)\/10/)
			{ 
				print "rating=\"$1\" ";last;
			}
			if($content_temp =~ m/rating-ineligible\"><a href=\"(.*)\">(.*)</)     
			{ 
				print "rating=\"Not released yet.\" ";last;
			}
		
			if($content_temp =~ m/Awaiting enough ratings/)
			{ 
				print "rating=\"Awaiting enough ratings\" ";last;
			}
		}
		
		if($content_temp =~ m/a href=\"(.*)\" title=\"(.*) \((.*)\)\"><img src=\"(.*)\" height/)
		{
			print "link=\"http://www.imdb.com$1\"/>";
		}
		print "\n";
		$count++;
	}

print "</movies>\n";
}