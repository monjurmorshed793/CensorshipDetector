/* tslint:disable max-line-length */
import { ComponentFixture, TestBed, inject, fakeAsync, tick } from '@angular/core/testing';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';
import { Observable, of } from 'rxjs';
import { JhiEventManager } from 'ng-jhipster';

import { CensorshipDetectorTestModule } from '../../../test.module';
import { WebAddressDeleteDialogComponent } from 'app/entities/web-address/web-address-delete-dialog.component';
import { WebAddressService } from 'app/entities/web-address/web-address.service';

describe('Component Tests', () => {
    describe('WebAddress Management Delete Component', () => {
        let comp: WebAddressDeleteDialogComponent;
        let fixture: ComponentFixture<WebAddressDeleteDialogComponent>;
        let service: WebAddressService;
        let mockEventManager: any;
        let mockActiveModal: any;

        beforeEach(() => {
            TestBed.configureTestingModule({
                imports: [CensorshipDetectorTestModule],
                declarations: [WebAddressDeleteDialogComponent]
            })
                .overrideTemplate(WebAddressDeleteDialogComponent, '')
                .compileComponents();
            fixture = TestBed.createComponent(WebAddressDeleteDialogComponent);
            comp = fixture.componentInstance;
            service = fixture.debugElement.injector.get(WebAddressService);
            mockEventManager = fixture.debugElement.injector.get(JhiEventManager);
            mockActiveModal = fixture.debugElement.injector.get(NgbActiveModal);
        });

        describe('confirmDelete', () => {
            it('Should call delete service on confirmDelete', inject(
                [],
                fakeAsync(() => {
                    // GIVEN
                    spyOn(service, 'delete').and.returnValue(of({}));

                    // WHEN
                    comp.confirmDelete(123);
                    tick();

                    // THEN
                    expect(service.delete).toHaveBeenCalledWith(123);
                    expect(mockActiveModal.dismissSpy).toHaveBeenCalled();
                    expect(mockEventManager.broadcastSpy).toHaveBeenCalled();
                })
            ));
        });
    });
});
