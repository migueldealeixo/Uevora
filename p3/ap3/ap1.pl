membro(X,[X|_]).

membro(X,[_|T]):-membro(X,T).

prefixo([],_).
prefixo([H|T1],[H|T2]):-prefixo(T1,T2).

sufixo([],_).
sufixo(L1,[_|T]):-sufixo(L1,T).

sufixo(L1,L1).

sublista([],_).
sublista(L1,[H|T]):- prefixo(L1,[H|T]).
sublista(L1,[_|T]):- sublista(L1,T).

tamanho([],z).
tamanho([_|T],N):-tamanho(T,N1), N is s(N1).
tamanho(L,T):-tamanho_acc(L,z,T).
tamanho_acc([],Acc,Acc).
tamanho_acc([_|T],Acc,Tamanho) :- tamanho_acc(T,s(Acc), Tamanho).