package org.odata4j.producer.jpa;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;

public class CommitTransactionCommand implements Command {

  @Override
  public boolean execute(JPAContext context) {
    EntityManager em = context.getEntityManager();
    EntityTransaction tx = em.getTransaction();
    tx.commit();
    context.setEntityTransaction(null);

    return false;
  }
}