Keep in View
===========

## Tucano (Easy begin)
Copyright &copy; 2012, Gustavo Liberatti. All rights reserved.

### Description
A study case compiler for Lalg

### Dependencies
- Java JDK 1.6 

### Codes for Lalg

program teste
var a,b:real;

procedure teste(a:real, b:real)
	var lb:real;
	var la: real;
        la=a;
	lb=b;
	write ((la+lb));
end;

begin
   teste(1,2)
end.


program exemplo2
	{exemplo2}
	var a: real;
	var b:integer;
	procedure nomep(x:real)
	  var a,c:integer;
	  begin
	    read(c,a);
	    if a < x + c then
	       a:= c+x;
               write(a)
	    else c:=a+x
	    $
end
begin{programa principal}
	read(b);
	nomep(b)
end.
