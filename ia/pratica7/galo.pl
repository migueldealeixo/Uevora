% Estado inicial
estado_inicial(e([v,v,v,v,v,v,v,v,v], x)).

% Jogador inverso
inv(x,o).
inv(o,x).

% Jogada: aplica jogada na posição C
op1(e(L,J),(C),e(L1,J1)) :- inv(J,J1), subs(v,J,L,L1,1,C).

% Substituição do valor na posição C
subs(A,J, [A|R], [J|R],C,C).
subs(A,J, [B|R], [B|S],N,C) :- M is N+1, subs(A,J,R,S,M,C).

% Estados terminais (vitória)
terminal(e([O,O,O,_,_,_,_,_,_],_)) :- O==x; O==o.
terminal(e([_,_,_,O,O,O,_,_,_],_)) :- O==x; O==o.
terminal(e([_,_,_,_,_,_,O,O,O],_)) :- O==x; O==o.
terminal(e([O,_,_,O,_,_,O,_,_],_)) :- O==x; O==o.
terminal(e([_,O,_,_,O,_,_,O,_],_)) :- O==x; O==o.
terminal(e([_,_,O,_,_,O,_,_,O],_)) :- O==x; O==o.
terminal(e([O,_,_,_,O,_,_,_,O],_)) :- O==x; O==o.
terminal(e([_,_,O,_,O,_,O,_,_],_)) :- O==x; O==o.

% Empate
terminal(e(L,_)) :- \+ member(v,L).

% Valor de estado terminal
valor(e(L,_), 0, _) :- \+ member(v,L), !.
valor(E,V,P) :- terminal(E),
    X is P mod 2,
    (X =:= 1 -> V = 1 ; V = -1).

% Linhas do tabuleiro
linha(1,2,3). linha(4,5,6). linha(7,8,9).
linha(1,4,7). linha(2,5,8). linha(3,6,9).
linha(1,5,9). linha(3,5,7).

% Verifica se uma linha é potencial para o jogador
linha_potencial(J, L, I1,I2,I3) :-
  nth(I1, L, A), nth(I2, L, B), nth(I3, L, C),
  include_not_v([A,B,C], PV),
  todos_iguais(J, PV).

% Conta quantas linhas potenciais um jogador tem
conta_possibilidades(J, L, N) :-
  findall(1,
    (linha(I1,I2,I3), linha_potencial(J, L, I1,I2,I3)),
    Ls),
  length(Ls, N).

% Avaliação heurística
avaliar(e(L,_), V) :-
  conta_possibilidades(x, L, VX),
  conta_possibilidades(o, L, VO),
  V is VX - VO.

% Minimax com cutoff
minimax_cut(E, P, _, V) :- terminal(E), valor(E, V, P).
minimax_cut(E, P, L, V) :- P >= L, avaliar(E, V).
minimax_cut(E, P, L, V) :-
  findall(E1, op1(E, _, E1), Es),
  P1 is P + 1,
  avalia_lista(Es, P1, L, Vs),
  escolhe_valor(P, Vs, V).

% Avalia uma lista de estados
avalia_lista([], _, _, []).
avalia_lista([E|Es], P, L, [V|Vs]) :-
  minimax_cut(E, P, L, V),
  avalia_lista(Es, P, L, Vs).

% Escolhe max ou min dependendo de P
escolhe_valor(P, Vs, V) :-
  P mod 2 =:= 0 -> max_list(Vs, V) ; min_list(Vs, V).

% Auxiliares
include_not_v([], []).
include_not_v([v|T], R) :- include_not_v(T, R).
include_not_v([H|T], [H|R]) :- H \= v, include_not_v(T, R).

todos_iguais(_, []).
todos_iguais(X, [H|T]) :- X = H, todos_iguais(X, T).

% nth1 compatível com gprolog
nth(1, [X|_], X).
nth(N, [_|T], X) :- N > 1, N1 is N-1, nth(N1, T, X).
