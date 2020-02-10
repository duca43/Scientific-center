package org.scientificcenter.service.handler;

import lombok.extern.slf4j.Slf4j;
import org.camunda.bpm.engine.delegate.DelegateTask;
import org.camunda.bpm.engine.delegate.TaskListener;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class AddCoauthorCreateHandler implements TaskListener {

    @Override
    public void notify(final DelegateTask delegateTask) {
        log.info("{} task - create listener", delegateTask.getName());
        delegateTask.getExecution().setVariable("name", null);
        delegateTask.getExecution().setVariable("email", null);
        delegateTask.getExecution().setVariable("location", null);
        delegateTask.getExecution().setVariable("wantMoreCoauthors", null);
    }
}