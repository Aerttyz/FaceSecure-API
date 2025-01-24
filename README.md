
# FaceSecure-API

API de reconhecimento facial com **Opencv** e modelos treinados a partir da biblioteca **opencv_contrib**.


## Tecnologias

- **JAVA 17**
- **Spring Boot**
- **Opencv**


## Funcionalidades

- Treinamento de modelos para reconhecimento facial
- Reconhecimento facial a partir de foto
- Reconhecimento facial a partir da WebCam
- Monitoramento Contínuo 

## Documentação da API

### Registra um usuário e cria seu diretório de imagens 

- Posteriormente, é necessário adicionar fotos a esse novo diretório para que sejam utilizadas no treinamento de modelo

```http
  POST /user/register
```
| Parâmetro   | Tipo       | Descrição                           |
| :---------- | :--------- | :---------------------------------- |
| `Name` | `string` |  Nome do usuário |

#### Inicia treinamento de modelo do zero com base nos usuários adicionados

```http
  GET /train
```

#### Inicia a atualização de modelo previamente treinado

```http
  GET /addNewData
```

#### Recebe uma imagem e utiliza do modelo treinado para fazer o reconhecimento facial

```http
  POST /recognize
```

### Inicia o processo de reconhecimento facial utilizando a webcam

```http
  GET /faceCam
```

### Inicia o monitoramento

```http
  GET /startMonitoring
```

| Parâmetro   | Tipo       | Descrição                           |
| :---------- | :--------- | :---------------------------------- |
| `minutes` | `int` | **Obrigatório**. Frequência com que o monitoramento vai realizar o reconhecimento facial |
| `timeLimit` | `int` | **Obrigatório**. Tempo total do monitoramento |


## Configuração

Algumas configurações no código precisam ser feitas para o bom funcionamento


- ```Atualize diretórios:``` Algumas funções utilizam de diretórios para funcionamento, para garantir que funcionem, procure por ```E:``` e substitua por diretórios da sua máquina;
- Certifique-se de que o arquivo ```opencv_480.dll``` está sendo referenciado corretamente na sua compilação.

    
## Melhorias

- Em versões futuras, adicionaremos autenticação com ```JWT token``` e reconhecimento facial


## Referência

 - [Opencv](https://github.com/opencv/opencv)
 - [opencv_contrib](https://github.com/opencv/opencv_contrib)
 - [How to write a Good readme](https://bulldogjob.com/news/449-how-to-write-a-good-readme-for-your-github-project)


