num(z).
num(s(X)):- num(X).

soma(z, Y, Y).                    
soma(s(X), Y, s(Z)) :- soma(X, Y, Z).  

sub(X,z,X).
sub(s(X),s(Y),Z):-
        sub(X,Y,Z).

le(z,s(Y)).
le(s(X),s(Y)):-le(X,Y).

ln([],z).
ln([_|T],s(N)):-ln(T,N).


mult(_,z,z).
mult(X,s(Y),Z):-
    mult(X,Y,W),
    soma(W,X,Z).

ln([],z).
ln([_|T],s(N)):-ln(T,N).

divisivel(X,z):-X  =0.
divisivel(X,Y):-
    le(Y,X),
    sub(X,Y,Z),
    divisivel(Z,Y).


