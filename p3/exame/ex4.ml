type 'a abp =
|Nada
|Coisa of 'a * 'a abp * 'a abp;;

let arvore = 
    Coisa(10,
      Coisa(2,Nada,Nada),
      Coisa(7,Nada,Nada);
    Coisa(12,Nada,Nada),
      Coisa(12,Nada,Nada);
      Coisa(20,Nada,Nada);
    );;

  let rec membro x  =function
  | Nada -> false
  | Coisa (v,esq,dir)->
      if x = v then true,
      else if x <v then membro x esq
      else membro x dir;;