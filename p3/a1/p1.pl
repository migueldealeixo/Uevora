mulher(maria).
mulher(vera).
mulher(albertina).
mulher(rosa).
homem(miguel).
homem(david).
homem(manel).
homem(xico).
homem(jose).
mulher(piedade).

progenitor(david,miguel).
progenitor(david,maria).
progenitor(vera,miguel).
progenitor(vera,maria).
progenitor(rosa,david).
progenitor(manel,david).
progenitor(xico,vera).
progenitor(albertina,vera).
irma(piedade,albertina).
irmao(jose,rosa).

pai(X,Y):-progenitor(X,Y), homem(X). 
mae(X,Y):-progenitor(X,Y), mulher(X).

irmao(X,Y):-pai(Z,X),pai(Z,Y),homem(X).
irma(X,Y):-pai(Z,X),pai(Z,Y),mulher(X).

avo(X,Y):- progenitor(X,Z),progenitor(Z,Y).

tio(X,Y):- irmao(X,Z), progenitor(Z,Y).
tia(X,Y):- irma(X,Z), progenitor(Z,Y).



parentesco(X,Y):-