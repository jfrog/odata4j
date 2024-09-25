package org.odata4j.producer.jpa;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;

public class BeginTransactionCommand implements Filter {

  @Override
  public boolean execute(JPAContext context) {
    EntityManager em = context.getEntityManager();
    EntityTransaction tx = em.getTransaction();
    tx.begin();
    context.setEntityTransaction(tx);

    return false;
  }

  public boolean postProcess(JPAContext context, Exception ex) {
    EntityTransaction tx = context.getEntityTransaction();
    if (tx != null) {
      if (tx.isActive()) {
        tx.rollback();
      } else {
        context.setEntityTransaction(null);
      }
    }
    return false;
  }
}