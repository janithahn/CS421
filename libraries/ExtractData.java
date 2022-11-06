package org.magnos.trie;

import com.google.common.reflect.ClassPath;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.NotFoundException;
import javassist.bytecode.*;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.io.FileWriter;
import java.io.IOException;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.stream.Collectors;

public class ExtractData {

    private static JSONArray packageList = new JSONArray();
    private static JSONArray classList = new JSONArray();

    private static JSONObject packageDetails = new JSONObject();

    public static void main(String[] args) throws Exception {


        Set<Class> classes = findAllClassesUsingGoogleGuice("org.magnos.trie");
        Set<ClassFile> classFiles = insertClassesToClassFiles(classes);

        getClassData(classFiles);

        packageDetails.put("Package Name", "org.magnos.trie");
        packageDetails.put("Classes", classList);
        packageList.add(packageDetails);

        writeJson("trie");

    }

    private static Set<Class> findAllClassesUsingGoogleGuice(String packageName) throws IOException {
        return ClassPath.from(ClassLoader.getSystemClassLoader())
                .getAllClasses()
                .stream()
                .filter(clazz -> clazz.getPackageName()
                        .equalsIgnoreCase(packageName))
                .map(clazz -> clazz.load())
                .collect(Collectors.toSet());
    }

    private static Set<ClassFile> insertClassesToClassFiles(Set<Class> classes) throws NotFoundException {
        ClassPool pool = ClassPool.getDefault();
        Set<ClassFile> classFiles = new HashSet<>();

        Iterator<Class> it = classes.iterator();
        while(it.hasNext()){
            String className = it.next().getName();
            pool.insertClassPath(className.replaceAll("[.]", "/"));
            CtClass ctClass = pool.getCtClass(className);
            ClassFile cf = ctClass.getClassFile();
            classFiles.add(cf);
        }

        return classFiles;
    }

    private static void getClassData(Set<ClassFile> classFiles) {

        Iterator<ClassFile> it = classFiles.iterator();
        while(it.hasNext()) {
            JSONObject classDetails = new JSONObject();
            JSONArray fieldList = new JSONArray();
            JSONArray methodList = new JSONArray();

            ClassFile cf = it.next();

            // Fields
            for (Object fieldInfoObj : cf.getFields()) {
                JSONObject fieldDetails = new JSONObject();

                try {
                    FieldInfo fieldInfo = (FieldInfo) fieldInfoObj;
                    fieldDetails.put("Name", fieldInfo.getName());
                    fieldDetails.put("Descriptor", fieldInfo.getDescriptor());
                    fieldList.add(fieldDetails);
                }catch (Exception e) {
                    e.printStackTrace();
                }
            }

            // Methods
            for (Object m : cf.getMethods()) {
                JSONObject methodDetails = new JSONObject();
                JSONArray localMethodParamList = new JSONArray();
                JSONArray localMethodVarList = new JSONArray();

                try {
                    MethodInfo minfo = (MethodInfo) m;
                    methodDetails.put("Method Name", minfo.getName());

                    CodeAttribute ca = minfo.getCodeAttribute();
                    LocalVariableAttribute table = (LocalVariableAttribute) ca.getAttribute(LocalVariableAttribute.tag);
                    int pc = 0;
                    int n = table.tableLength();
                    for (int i = 0; i < n; ++i) {
                        JSONObject localMethodParamDetails = new JSONObject();
                        JSONObject localMethodVarDetails = new JSONObject();

                        int start = table.startPc(i);
                        int len = table.codeLength(i);

                        if (start <= pc && pc < start + len) {
                            if (!table.variableName(i).equals("this")) {
                                localMethodParamDetails.put("Name", table.variableName(i));
                                localMethodParamDetails.put("Descriptor", table.descriptor(i));
                                localMethodParamList.add(localMethodParamDetails);
                            }
                        } else {
                            localMethodVarDetails.put("Name", table.variableName(i));
                            localMethodVarDetails.put("Descriptor", table.descriptor(1));
                            localMethodVarList.add(localMethodVarDetails);
                        }
                    }

                    methodDetails.put("Parameters", localMethodParamList);
                    methodDetails.put("Variables", localMethodVarList);
                    methodList.add(methodDetails);
                }catch (Exception e) {
                    e.printStackTrace();
                }
            }

            classDetails.put("Class Name", cf.getName());
            classDetails.put("Methods", methodList);
            classDetails.put("Fields", fieldList);
            classList.add(classDetails);
        }
    }

    private static void writeJson(String fileName) {
        fileName = fileName + ".json";
        try (FileWriter file = new FileWriter(fileName)) {
            file.write(packageList.toJSONString());
            file.flush();
            System.out.println("Successfully written data to " + fileName);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
