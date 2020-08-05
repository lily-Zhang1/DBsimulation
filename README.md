# DBsimulation

根据BNF.txt定义的语法规则设计数据库。对用户输入进行解析后根据输入进行数据的存储查询等操作。数据用String存储在文件中，只使用核心java类来实现，没有使用jdbc。支持数据的操作包括：<Use> | <Create> | <Drop> | <Alter> | <Insert> | <Select> | <Update> | <Delete> | <Join>，具体如下所示。
  
# 语法规则
<Command>        ::=  <CommandType>;

<CommandType>    ::=  <Use> | <Create> | <Drop> | <Alter> | <Insert> |
                      <Select> | <Update> | <Delete> | <Join>

<Use>            ::=  USE <DatabaseName>

<Create>         ::=  <CreateDatabase> | <CreateTable>

<CreateDatabase> ::=  CREATE DATABASE <DatabaseName>

<CreateTable>    ::=  CREATE TABLE <TableName> | CREATE TABLE <TableName> ( <AttributeList> )

<Drop>           ::=  DROP <Structure> <StructureName>

<Structure>      ::=  DATABASE | TABLE

<Alter>          ::=  ALTER TABLE <TableName> <AlterationType> <AttributeName>

<Insert>         ::=  INSERT INTO <TableName> VALUES ( <ValueList> )

<Select>         ::=  SELECT <WildAttribList> FROM <TableName> |
                      SELECT <WildAttribList> FROM <TableName> WHERE <Condition> 

<Update>         ::=  UPDATE <TableName> SET <NameValueList> WHERE <Condition> 

<Delete>         ::=  DELETE FROM <TableName> WHERE <Condition>

<Join>           ::=  JOIN <TableName> AND <TableName> ON <AttributeName> AND <AttributeName>

<NameValueList>  ::=  <NameValuePair> | <NameValuePair> , <NameValueList>

<NameValuePair>  ::=  <AttributeName> = <Value>

<AlterationType> ::=  ADD | DROP

<ValueList>      ::=  <Value>  |  <Value> , <ValueList>

<Value>          ::=  '<StringLiteral>'  |  <BooleanLiteral>  |  <FloatLiteral>  |  <IntegerLiteral>

<BooleanLiteral> ::=  true | false

<WildAttribList> ::=  <AttributeList> | *

<AttributeList>  ::=  <AttributeName> | <AttributeName> , <AttributeList>

<Condition>      ::=  ( <Condition> ) AND ( <Condition> )  |
                      ( <Condition> ) OR ( <Condition> )   |
                      <AttributeName> <Operator> <Value>

<Operator>       ::=   ==   |   >   |   <   |   >=   |   <=   |   !=   |   LIKE
