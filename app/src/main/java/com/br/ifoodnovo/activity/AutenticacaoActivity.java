package com.br.ifoodnovo.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.Toast;

import com.br.ifoodnovo.R;
import com.br.ifoodnovo.helpers.ConfiguracaoFirebase;
import com.br.ifoodnovo.helpers.UsuarioFirebase;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.google.firebase.auth.FirebaseUser;

public class AutenticacaoActivity extends AppCompatActivity {

    // Instanciando os elementos do código
    private Button btnAcessar;
    private EditText campoEmail, campoSenha;
    private Switch tipoAcesso, tipoUsuario;
    private LinearLayout linearTipoUsuario;
    private FirebaseAuth autenticacao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_autenticacao);

        // Chamada de componentes
        inicializaComponentes();

        // Configurando e/ou chamando o Firebase nesse arquivo
        autenticacao = ConfiguracaoFirebase.getReferenciaAutenticacao();

        // Verifica usuario logado
        verificaUsuarioLogado();

        // Verifica qual a opção do switch tipo acesso(logar/cadastrar) está marcada
        tipoAcesso.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                if (isChecked){ // Habilita o novo switch de usuário ou empresa
                    linearTipoUsuario.setVisibility(View.VISIBLE);
                }else{ // Ignora o switch e entra conforme o tipo (usuário/empresa)
                    linearTipoUsuario.setVisibility(View.GONE);
                }
            }
        });

        // Configuração o Botão Acessar
        btnAcessar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                // Recupera as informações digitadas
                String email = campoEmail.getText().toString();
                String senha = campoSenha.getText().toString();

                // Verifica o Email
                if (!email.isEmpty()) {
                    // Verifica a Senha
                    if (!senha.isEmpty()) {

                        // Verificmos o estado dp Switch
                        if (tipoAcesso.isChecked()) { // Cadastro
                            autenticacao.createUserWithEmailAndPassword(
                                    email, senha
                            ).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if (task.isSuccessful()) {
                                        // Caso o cadastro seja realizado com sucesso
                                        Toast.makeText(AutenticacaoActivity.this,
                                                "Cadastro realizado com Sucesso!",
                                                Toast.LENGTH_SHORT).show();

                                        // Recupenando o tipo do usuário
                                        String tipoUsuario = getTipoUsuario();
                                        UsuarioFirebase.atualizarTipoUsuario(tipoUsuario);

                                        // Chama a abertura da tela baseada no tipo de usuário
                                        abrirTelaPrincipal(tipoUsuario);

                                    } else {
                                        // Em caso de erro,  mostrar as mensagens correspondentes
                                        String erroExcecao = "";

                                        try {
                                            throw task.getException();
                                        } catch (FirebaseAuthWeakPasswordException e) {
                                            erroExcecao = "Digite uma senha mais forte";
                                        } catch (FirebaseAuthInvalidCredentialsException e) {
                                            erroExcecao = "Por favor, digite um e-mail válido";
                                        } catch (FirebaseAuthUserCollisionException e) {
                                            erroExcecao = "E-mail já cadastrado!";
                                        } catch (Exception e) {
                                            erroExcecao = "ao cadastrar usuário: " + e.getMessage();
                                        }

                                        // Montagem da mensagem em caso de erro
                                        Toast.makeText(AutenticacaoActivity.this,
                                                "Erro: " + erroExcecao,
                                                Toast.LENGTH_SHORT).show();

                                    }
                                }
                            });
                        } else { // Login
                            autenticacao.signInWithEmailAndPassword(
                                    email, senha
                            ).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if (task.isSuccessful()) {
                                        // Mensagem de Sucesso
                                        Toast.makeText(AutenticacaoActivity.this,
                                                "Logado com Sucesso!",
                                                Toast.LENGTH_SHORT).show();

                                        // Recuperando tipo de usuario
                                        String tipoUsuario = task.getResult().getUser().getDisplayName();

                                        // Chamada tela principal
                                        abrirTelaPrincipal(tipoUsuario);

                                    } else {
                                        // Mensagem de erro
                                        Toast.makeText(AutenticacaoActivity.this,
                                                "Erro ao fazer o login" + task.getException(), Toast.LENGTH_SHORT).show();
                                    }

                                }
                            });
                        }

                    } else {
                        // Mensagem no caso a senha estiver Vazia
                        Toast.makeText(AutenticacaoActivity.this,
                                "Preencha a senha!", Toast.LENGTH_SHORT).show();
                    }

                } else {
                    // Mensagem em caso de e-mail Vazio
                    Toast.makeText(AutenticacaoActivity.this,
                            "Preencha o e-mail", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    // Método responsável por verificar usuario logado
    private void verificaUsuarioLogado(){
        FirebaseUser usuarioAtual = autenticacao.getCurrentUser();
        if (usuarioAtual != null){
            String tipoUsuario = usuarioAtual.getDisplayName();
            abrirTelaPrincipal(tipoUsuario);
        }
    }

    // Método que verifica se o switch do Usuario/Empresa está marcado
    private String getTipoUsuario(){return tipoUsuario.isChecked() ? "E" : "U";} // E = empresa   U= usuario

    // Método responsável por abrir a tela principal "Home"
    public void abrirTelaPrincipal(String tipoUsuario) {
        if (tipoUsuario.equals("E")){ // Empresa
            startActivity(new Intent(getApplicationContext(), EmpresaActivity.class));
            finish();
        } else { // Usuario
            startActivity(new Intent(getApplicationContext(), HomeActivity.class));
            finish();
        }
    }

    // Inicializando os componentes
    private void inicializaComponentes() {
        campoEmail = findViewById(R.id.edtCadastroEmail);
        campoSenha = findViewById(R.id.edtCadastroSenha);
        btnAcessar = findViewById(R.id.btnAcessar);
        tipoAcesso = findViewById(R.id.switchAcesso);
        tipoUsuario = findViewById(R.id.switcTipoUsuario);
        linearTipoUsuario = findViewById(R.id.linearTipoUsuario);
    }

}