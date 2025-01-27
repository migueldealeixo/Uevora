let rec conta elemento lista= 
match lista with
| []-> 0
| x::xs->
    if x = elemento then 1 + conta elemento xs
    else conta elemento xs;;

    let () =
    print_endline "Teste 1:";
    let resultado1 = conta 3 [1; 2; 3; 3; 4; 5] in
    print_endline ("Resultado: " ^ string_of_int resultado1);
  
