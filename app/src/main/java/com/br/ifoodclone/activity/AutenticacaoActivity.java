package com.br.ifoodclone.activity;

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

import com.br.ifoodclone.R;
import com.br.ifoodclone.helpers.ConfiguracaoFirebase;
import com.br.ifoodclone.helpers.usuarioFirebase;
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
    private LinearLayout LinearTipoUsuario;
    private FirebaseAuth autenticacao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_autenticacao);


        // Chamando os componentes
        inicializaComponentes();
        autenticacao= ConfiguracaoFirebase.getFirebaseAutenticacao();
       // autenticacao.signOut();
        verificarUsuarioLogado();
        tipoAcesso.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){ // empresa
                    LinearTipoUsuario.setVisibility(View.VISIBLE);
                }else{ // usuário
                    LinearTipoUsuario.setVisibility(View.GONE);
                }
            }
        });

        // Configurando o Botão Acessar
        btnAcessar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // Recuperando as informações digitadas
                String email = campoEmail.getText().toString();
                String senha = campoSenha.getText().toString();

                if (!email.isEmpty()) {// Verifica o Email
                    if (!senha.isEmpty()) {// Verifica a Senha
                        if (tipoAcesso.isChecked()) { // Cadastro // Verificamos o estado do Switch
                            autenticacao.createUserWithEmailAndPassword(email, senha).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if(task.isSuccessful()) {
                                        // Caso o cadastro seja realizado com sucesso
                                        Toast.makeText(AutenticacaoActivity.this,
                                                "Cadastro realizado com sucesso!",
                                                Toast.LENGTH_SHORT).show();
                                        String tipousuario = getTipoUsuario();
                                        usuarioFirebase.atualizarTipousuario(tipousuario);
                                        abrirTelaPrincipal(tipousuario);    // Chama a tela de Home

                                    } else {
                                        // Em caso de erro, mostrar as mensagens correspondentes
                                        String erroExcecao = "";

                                        try {
                                            throw task.getException();
                                        } catch (FirebaseAuthWeakPasswordException e) {
                                            erroExcecao = "Digite uma senha mais forte!";
                                        } catch (FirebaseAuthInvalidCredentialsException e) {
                                            erroExcecao = "Por favor, digite um e-mail válido!";
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
                            autenticacao.signInWithEmailAndPassword(email, senha).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if (task.isSuccessful()){
                                        // Mensagem de Sucesso
                                        Toast.makeText(AutenticacaoActivity.this,
                                                "Logado com sucesso!",
                                                Toast.LENGTH_SHORT).show();
                                        String tipoUsuario =task.getResult().getUser().getDisplayName();
                                        abrirTelaPrincipal(tipoUsuario);    // Chamando a tela principal
                                    } else {
                                        // Mensagem de Erro
                                        Toast.makeText(AutenticacaoActivity.this,
                                                "Erro ao fazer login!" + task.getException(), Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                        }

                    } else {
                        // Mensagem em caso de Senha Vazia
                        Toast.makeText(AutenticacaoActivity.this,
                                "Preencha a senha!", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    // Mensagem em caso de E-mail Vazio
                    Toast.makeText(AutenticacaoActivity.this,
                            "Preencha o e-mail!", Toast.LENGTH_SHORT).show();
                }
            }

        });

    }

    private String getTipoUsuario(){
        return tipoUsuario.isChecked()?"E" : "U";
    }

    private void verificarUsuarioLogado(){
        FirebaseUser usuarioLogado = autenticacao.getCurrentUser();
        if( usuarioLogado != null){
            String tipoUsuario = usuarioLogado.getDisplayName();
            abrirTelaPrincipal(tipoUsuario);
        }
    }


    // Método responsável por abrir a tela principal
    private void abrirTelaPrincipal(String tipoUsuario) {
        if(tipoUsuario.equals("E")){
            startActivity(new Intent(getApplicationContext(), EmpresaActivity.class));
            finish();
        }else {
            startActivity(new Intent(getApplicationContext(), HomeActivity.class));
            finish();
        }
    }

    public void recupera(View View){
        startActivity(new Intent(getApplicationContext(),RecuperaSenhaActivity.class));
    }

    // Inicializando os componentes
    private void inicializaComponentes(){
        campoEmail = findViewById(R.id.editCadastroEmail);
        campoSenha = findViewById(R.id.editCadastroSenha);
        btnAcessar = findViewById(R.id.btnAcessar);
        tipoAcesso = findViewById(R.id.switchAcesso);
        tipoUsuario = findViewById(R.id.switchAcessoUsuario);
        LinearTipoUsuario = findViewById(R.id.LinearTipoUsuario);
    }
}