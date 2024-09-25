package org.odata4j.producer.jpa;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;

public class EntityManagerCommand implements Filter {

  private final EntityManagerFactory emf;

  public EntityManagerCommand(EntityManagerFactory emf) {
    this.emf = emf;
  }

  @Override
  public boolean execute(JPAContext context) {
    EntityManager em = this.emf.createEntityManager();
    context.setEntityManager(em);

    return false;
  }

  @Override
  public boolean postProcess(JPAContext context, Exception exception) {
    context.getEntityManager().close();
    context.setEntityManager(null);

    return false;
  }
}