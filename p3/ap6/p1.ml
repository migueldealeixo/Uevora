

let rec membro lista elemento=
  match lista with
  | [] -> false
  | x :: xs -> if x = elemento then true else membro xs elemento;;

  let () =
  let resultado = membro [1; 2; 3] 2 in
  print_endline (string_of_bool resultado);; (* Resultado: true *)
