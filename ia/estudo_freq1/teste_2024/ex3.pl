% Símbolos permitidos no quadrado latino
simbolo(a).
simbolo(b).
simbolo(c).

% Verifica se todos os elementos de uma lista são distintos
todos_diferentes([]).
todos_diferentes([X | R]) :- \+ member(X, R), todos_diferentes(R).

% Permutação de símbolos para formar linhas do quadrado
linha([X, Y, Z]) :- 
    simbolo(X), simbolo(Y), simbolo(Z),
    todos_diferentes([X, Y, Z]).

% Definição do quadrado latino de ordem 3
quadrado_latino([
    [A1, A2, A3],
    [B1, B2, B3],
    [C1, C2, C3]
]) :-
    % Geração das linhas com permutações válidas
    linha([A1, A2, A3]),
    linha([B1, B2, B3]),
    linha([C1, C2, C3]),

    % Restrições de colunas
    todos_diferentes([A1, B1, C1]),
    todos_diferentes([A2, B2, C2]),
    todos_diferentes([A3, B3, C3]),

    % Restrições das diagonais principais
    todos_diferentes([A1, B2, C3]),
    todos_diferentes([A3, B2, C1]),

    % Exibir solução
    writeln([[A1, A2, A3], [B1, B2, B3], [C1, C2, C3]]).

