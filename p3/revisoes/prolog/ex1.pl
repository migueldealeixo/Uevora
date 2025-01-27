mulher(maria).
mulher(vera).
mulher(rosa).
mulher(albertina).
homem(miguel).
homem(david).
homem(manel).
homem(xico).

progenitor(david,maria).
progenitor(vera,maria).
progenitor(david,miguel).
progenitor(vera,miguel).
progenitor(albertina,vera).
progenitor(xico,vera).
progenitor(rosa,david).
progenitor(manel,david).


%1

pai(X,Y):-progenitor(X,Y), homem(X).

%2

mae(X,Y):-progenitor(X,Y), mulher(X).

%3

irmao(X,Y):- progenitor(Z,X),progenitor(Z,Y), homem(X), X\=Y.

%4

irma(X,Y):- progenitor(Z,Y), progenitor(Z,X), mulher(X),X\=Y.

%5

avo(X,Y):- progenitor(X,Z), progenitor(Z,Y).


%13

grau(X,Y,1).
grau(X,Y,N):-
    progenitor(X,Z),
    grau(Z,Y,M),
    N is M+1.



e(lisboa, santarem).
e(santarem, coimbra).
e(santarem, caldas).
e(caldas, lisboa).
e(coimbra, porto).
e(lisboa, evora).
e(evora, beja).
e(lisboa, faro).
e(beja, faro).

existe(X,Y):- e(X,Y).

caminhos(X,Y,1).
caminhos(X,Y,N):-
    existe(Z,Y,M),
    existe(X,W),
    N is M+1,
    caminhos(X,W,P),
    P > 0,
    N is P+1.
