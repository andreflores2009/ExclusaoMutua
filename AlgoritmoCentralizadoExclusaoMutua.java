/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 */

package com.algoritmoCentralizadoExclusaoMutua;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

/**
 *
 * @author Andre
 */
public class AlgoritmoCentralizadoExclusaoMutua {

    /*BlockingQueue<Integer>: Esse é o tipo de dado usado para armazenar as requisições dos processos. 
        O BlockingQueue é uma estrutura de dados que permite operações de inserção (put) e remoção (take) de elementos,
        com bloqueio. Isso significa que, se a fila estiver cheia, qualquer operação de inserção vai esperar até 
        que haja espaço disponível. Da mesma forma, se a fila estiver vazia, a operação de remoção espera até 
        que haja um elemento para ser removido.
    Usar BlockingQueue é uma boa escolha aqui porque queremos garantir que os processos sejam tratados de forma síncrona,
        ou seja, um processo precisa esperar pelo outro antes de acessar a seção crítica.
    ArrayBlockingQueue<>(1): O número 1 especifica a capacidade da fila, ou seja, quantos elementos ela pode 
        armazenar simultaneamente. Neste caso, a fila pode conter apenas um elemento por vez. Isso significa que apenas 
        um processo por vez pode estar "na fila" para acessar a seção crítica.
    Se outro processo tentar se colocar na fila enquanto já há um processo esperando, ele ficará bloqueado até 
        que o primeiro processo saia da fila (ou seja, até que tenha acessado e liberado a seção crítica).
    
    O uso de BlockingQueue facilita o controle de acesso à seção crítica porque:
    */
    // Fila de requisições para controlar os processos que desejam acessar a seção crítica
    private static final BlockingQueue<Integer> filaRequisicoes = new ArrayBlockingQueue<>(1);
    
    // Objeto para garantir a exclusão mútua (sincronização entre threads)
    /*O que é o lock?
    O lock aqui é uma referência para um objeto (Object lock = new Object();) que usamos para garantir 
    que a execução de um bloco de código seja feita por apenas uma thread por vez.
    No caso, estamos sincronizando o acesso à seção crítica de forma que, enquanto um processo (ou thread) 
    está dentro do bloco sincronizado, nenhum outro processo pode entrar nele. 
    Outros processos que tentarem acessar a seção crítica nesse meio tempo ficam bloqueados até que 
    o processo atual termine sua execução e libere o bloqueio.
    */
    private static final Object lock = new Object();
    
    public static void main(String[] args) throws InterruptedException {
        // Criação de três processos que vão competir pela seção crítica
        Thread processo1 = new Thread(new Processo(1));
        Thread processo2 = new Thread(new Processo(2));
        Thread processo3 = new Thread(new Processo(3));
        
        // Iniciar a execução dos processos
        processo1.start();
        processo2.start();
        processo3.start();
        
        // Esperar todos os processos terminarem
        processo1.join();
        processo2.join();
        processo3.join();
    }
    
    /*
    Por que usar classes internas?: Classes internas são frequentemente usadas quando a classe interna tem
    uma forte associação lógica com a classe externa. Neste exemplo, Processo está associado diretamente ao
    funcionamento dos algoritmos de exclusão mútua, e portanto faz sentido colocá-lo dentro da classe principal
    para manter a organização do código.
    */
    static class Processo implements Runnable {
        private int id;

        public Processo(int id) {
            this.id = id;
        }

        @Override
        public void run() {
            try {
                System.out.println("Processo " + id + " solicitando acesso à seção crítica.");
                // O processo entra na fila de requisições para pedir o acesso à seção crítica
                filaRequisicoes.put(id);
                synchronized (lock) {
                    acessarSecaoCritica();
                }
                /*synchronized (lock): Este é o mecanismo de bloqueio que Java fornece para controlar o 
                acesso a recursos compartilhados. A ideia é que quando um processo entra em um bloco 
                de código marcado com synchronized, ele adquire um "bloqueio" no objeto especificado 
                (no caso, o objeto lock). Enquanto ele estiver dentro do bloco, nenhum outro processo pode 
                adquirir esse mesmo bloqueio.
                */
                
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }

        private void acessarSecaoCritica() throws InterruptedException {
            // Somente o processo que está na fila de requisições acessa a seção crítica
            
            Integer proximoProcesso = filaRequisicoes.peek();  // Verifica quem será o próximo na fila
            System.out.println("Processo " + proximoProcesso + " está na seção crítica.");
            
            Thread.sleep(1000);  // Simulando o trabalho na seção crítica
            
            Integer processoConcedido = filaRequisicoes.take();
            /*O método take() remove e retorna o primeiro elemento da fila (filaRequisicoes). 
            Se a fila estiver vazia no momento da chamada, o método bloqueia (ou seja, faz o processo esperar) 
            até que algum elemento esteja disponível para ser removido.*/            
            System.out.println("Processo " + processoConcedido + " saiu da seção crítica.");
        }
    }
      
}
