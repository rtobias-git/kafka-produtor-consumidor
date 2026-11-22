import os
import time

from confluent_kafka import Producer

ENDERECO_KAFKA = os.getenv("KAFKA_BOOTSTRAP_SERVERS", "kafka:9092")
TOPICO = os.getenv("KAFKA_TOPIC", "fila-pedidos")
PRATOS = ["pizza", "sushi", "hambúrguer", "lasanha", "salada",
          "tacos", "ramen", "churrasco", "risoto", "panqueca"]

produtor = Producer({"bootstrap.servers": ENDERECO_KAFKA})


def confirmar(erro, mensagem):
    if erro is not None:
        print(f"  ERRO: {erro}")
    else:
        print(f"  broker guardou na posicao {mensagem.offset()} da fila")


print("Produtor colocando pedidos na fila...")

numero = 1
for prato in PRATOS:
    pedido = f"Pedido {numero}: {prato}"

    produtor.produce(TOPICO, value=pedido.encode("utf-8"), callback=confirmar)
    produtor.poll(0)

    print(f"Produtor enviou: {pedido}")
    time.sleep(0.3)

    numero = numero + 1

produtor.flush()
print("Produtor acabou, os 10 pedidos estao na fila do broker")
