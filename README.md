# Compiladores
Repositório para a disciplina de Compiladores - IC (UFAL)

# Linguagem JORG

A linguagem de programação “JORG” é voltada ao desenvolvimento de programas e algoritmos simples, com valor para a aprendizagem de programação ao possibilitar o uso de conceitos como tipos primitivos de dados e estruturas de controle.

# Organização do Projeto

## Compiler

O diretório contêm a classe "Compiler" responsável por iniciar a aplicação. Neste momento do projeto, o arquivo analisa se algum arquivo foi informado para o processo de compilação e, ao informá-lo, começa a análise léxica referente aos lexemas presentes no programa.

## Lexical

O diretório contêm as classes "LexicalScanner", "Token" e "TokenCategory", onde LexicalScanner é a classe principal utilizada para fazer a análise léxica. Essa mesma classe contêm o autômato responsável pelas análises dos tokens definidos na especificação da linguagem. Ademais, é na classe TokenCategory que temos a enumeração e os tokens listados na especificação. Por fim, temos a classe Token que serve como um modelo para lidar com os elementos presentes nos programas.

## Programas

Como solicitado na primeira parte do projeto, temos os programas "fibonacci.jorg", "hello_world.jorg", "shell_sort.jorg" escritos na linguagem desenvolvida na disciplina.
# Membros

- Gabriel Luiz Leite Souza
- João Victor Falcão Santos Lima
- Rodrigo Santos da Silva