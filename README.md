# Ifood Clone



Este é um clone do APP Ifood com todas suas funcionalidades, mas com leves diferenças.


![image](https://user-images.githubusercontent.com/71250901/115285042-ca9ac500-a123-11eb-8ded-338d9a664124.png)


![Version](https://img.shields.io/badge/Version-1.0.0-F21B3F) ![Build](https://img.shields.io/badge/Build-Passing-29BF12) ![Projeto](https://img.shields.io/badge/Projeto-IfoodClone-08BDBD) ![Code_Quality](https://img.shields.io/badge/Code_Quality-Good-3A5683) ![Languange](https://img.shields.io/badge/Language-Java-F7DF1E) 


```java

  public class ConfiguracoesEmpresaActivity extends AppCompatActivity {

    private EditText editEmpresaNome, editEmpresaCategoria, editEmpresaTempo, editEmpresaTaxa;
    private ImageView imagemEmpresaPerfil;
    private static final int SELECAO_GALERIA = 2000;
    private StorageReference storageReference;
    private DatabaseReference firebaseRef;
    private String idUsuarioLogado;
    private String urlImagemSelecionada = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_configuracoes_empresa);

        inicializacomponentes();

        storageReference = ConfiguracaoFirebase.getFirebaseStorage();
        firebaseRef = ConfiguracaoFirebase.getFirebase();
        idUsuarioLogado = usuarioFirebase.getIdUsuario();


        // Configurações da toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("Configurações empresa");
        setSupportActionBar(toolbar);
        // Para mostrar a seta de voltar para home
        // Necessário configurar no AndroidManifests
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


```
## Como contribuir

Contribuições são sempre bem-vindas. Existem várias maneiras de contribuir com este projeto, como:

💪 Se juntando ao time de desenvolvimento.

🌟 Dando uma estrela no projeto.

🐛 Reportando um Bug.

😅 Indicando um vacilo que eu possa ter cometido.

📄 Ajudando a melhorar a documentação.

🚀 Compartilhando este projeto com seus amigos.


## Licença

MIT License

## Status do Projeto

Projeto em andamento.
 
