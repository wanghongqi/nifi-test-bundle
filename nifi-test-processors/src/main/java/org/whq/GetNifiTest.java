package org.whq;

import org.apache.nifi.annotation.documentation.CapabilityDescription;
import org.apache.nifi.annotation.documentation.Tags;
import org.apache.nifi.components.PropertyDescriptor;
import org.apache.nifi.expression.ExpressionLanguageScope;
import org.apache.nifi.flowfile.FlowFile;
import org.apache.nifi.processor.AbstractProcessor;
import org.apache.nifi.processor.ProcessContext;
import org.apache.nifi.processor.ProcessSession;
import org.apache.nifi.processor.Relationship;
import org.apache.nifi.processor.exception.ProcessException;
import org.apache.nifi.processor.io.OutputStreamCallback;
import org.apache.nifi.processor.util.StandardValidators;

import java.io.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@CapabilityDescription("NiFi processor for whq test.")
@Tags({"whq", "test"})
public class GetNifiTest extends AbstractProcessor {
    //外界参数说明
    public static final PropertyDescriptor TABLE_NAME = new PropertyDescriptor.Builder()
            .name("Table Name")
            .description("This is Table Name")
            .required(true)
            .addValidator(StandardValidators.NON_EMPTY_VALIDATOR)
            .expressionLanguageSupported(ExpressionLanguageScope.VARIABLE_REGISTRY)
            .build();
    public static final PropertyDescriptor SQL = new PropertyDescriptor.Builder()
            .name("sql")
            .description("This is SQL")
            .addValidator(StandardValidators.NON_EMPTY_VALIDATOR)
            .expressionLanguageSupported(ExpressionLanguageScope.VARIABLE_REGISTRY)
            .build();

    @Override
    protected List<PropertyDescriptor> getSupportedPropertyDescriptors(){
        final List<PropertyDescriptor> props=new ArrayList<>();
        props.add(TABLE_NAME);
        props.add(SQL);
        return props;
    }

    //输出关系
    public static final Relationship REL_SUCCESS = new Relationship.Builder()
            .name("success")
            .description("All FlowFiles that are created are routed to this relationship")
            .build();
    public static final Relationship REL_FAILURE = new Relationship.Builder()
            .name("failure")
            .description("All FlowFiles that are created are routed to this relationship")
            .build();
    @Override
    public Set<Relationship> getRelationships() {
        final Set<Relationship> relationships = new HashSet<>(1);
        relationships.add(REL_FAILURE);
        relationships.add(REL_SUCCESS);
        return relationships;
    }

    @Override
    public void onTrigger(ProcessContext context, ProcessSession session) throws ProcessException {
        FlowFile flowFile=session.create();
        if(flowFile==null){
            return;
        }
        try {
            //属性获取
            String tableName= context.getProperty(TABLE_NAME).getValue();
            //写入数据流
            session.write(flowFile, new OutputStreamCallback() {
                @Override
                public void process(OutputStream out) throws IOException {
                    out.write(("输出测试"+tableName).getBytes());
                }
            });
        } catch (ProcessException e){
            session.transfer(flowFile,REL_FAILURE);
        }
        session.transfer(flowFile, REL_SUCCESS);
    }
}
