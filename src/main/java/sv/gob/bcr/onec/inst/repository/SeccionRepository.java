package sv.gob.bcr.onec.inst.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import sv.gob.bcr.onec.inst.entity.Seccion;

import java.util.List;

public interface SeccionRepository extends JpaRepository<Seccion, Integer> {

    List<Seccion> findByFormulario_IdFormulario(Integer idFormulario);

    boolean existsByCodigoAndFormulario_IdFormulario(String codigo, Integer idFormulario);

    boolean existsByCodigoAndFormulario_IdFormularioAndIdSeccionNot(String codigo, Integer idFormulario, Integer idSeccion);
}
