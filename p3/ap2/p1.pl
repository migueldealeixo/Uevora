num(z).
num(s(z)):-num(X).


le(z,_).
le(s(X),s(Y)):-le(X,Y).
lt(z,_).
lt(s(X),s(Y)):-lt(X,Y).

soma(Y,z,Y).
soma(s(Y),Z,s(Y)):-soma(Y,Z,soma).

sub(Y,z,Y).
sub(s(X),s(Y),Z):-sub(X,Y,Z).

mult(_,z,z).

mult(s(X),Y,z):-
    mult(X,Y,Resultado).