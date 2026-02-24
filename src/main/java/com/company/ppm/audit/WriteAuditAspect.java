package com.company.ppm.audit;

import com.company.ppm.domain.entity.AuditLog;
import com.company.ppm.repository.AuditLogRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.lang.reflect.Method;
import java.security.Principal;
import java.util.Arrays;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@Aspect
@Component
public class WriteAuditAspect {

    private final AuditLogRepository auditLogRepository;
    private final ObjectMapper objectMapper;

    public WriteAuditAspect(AuditLogRepository auditLogRepository, ObjectMapper objectMapper) {
        this.auditLogRepository = auditLogRepository;
        this.objectMapper = objectMapper;
    }

    @Pointcut("@annotation(org.springframework.web.bind.annotation.PostMapping) || "
            + "@annotation(org.springframework.web.bind.annotation.PutMapping) || "
            + "@annotation(org.springframework.web.bind.annotation.PatchMapping) || "
            + "@annotation(org.springframework.web.bind.annotation.DeleteMapping)")
    public void writeMapping() {
    }

    @Around("within(@org.springframework.web.bind.annotation.RestController *) && writeMapping()")
    public Object aroundWriteEndpoint(ProceedingJoinPoint pjp) throws Throwable {
        Object result = pjp.proceed();
        persistAudit(pjp, result);
        return result;
    }

    private void persistAudit(ProceedingJoinPoint pjp, Object result) {
        ServletRequestAttributes attrs = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (attrs == null) {
            return;
        }

        AuditLog log = new AuditLog();
        Principal principal = attrs.getRequest().getUserPrincipal();
        if (principal != null) {
            log.setActorEmail(principal.getName());
        }

        MethodSignature signature = (MethodSignature) pjp.getSignature();
        Method method = signature.getMethod();
        log.setAction(method.getName());
        log.setResource(signature.getDeclaringType().getSimpleName());
        log.setResourceId(extractResourceId(result));
        log.setMethod(attrs.getRequest().getMethod());
        log.setPath(attrs.getRequest().getRequestURI());
        log.setPayload(safePayload(pjp.getArgs()));

        auditLogRepository.save(log);
    }

    private String extractResourceId(Object result) {
        if (result == null) {
            return null;
        }
        try {
            Method getId = result.getClass().getMethod("getId");
            Object id = getId.invoke(result);
            return id == null ? null : id.toString();
        } catch (Exception ignored) {
            return null;
        }
    }

    private String safePayload(Object[] args) {
        try {
            String payload = objectMapper.writeValueAsString(Arrays.stream(args)
                    .map(arg -> arg == null ? null : sanitize(arg))
                    .toList());
            return payload.length() > 3900 ? payload.substring(0, 3900) : payload;
        } catch (JsonProcessingException ex) {
            return "<unserializable>";
        }
    }

    private Object sanitize(Object arg) {
        String simpleName = arg.getClass().getSimpleName().toLowerCase();
        if (simpleName.contains("login") || simpleName.contains("password") || simpleName.contains("refresh")) {
            return "<redacted>";
        }
        return arg;
    }
}
