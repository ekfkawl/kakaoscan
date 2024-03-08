import React from 'react';
import { Accordion } from 'flowbite-react';

const Faq = () => {
    return (
        <div className="mx-auto max-w-screen-xl px-4 py-8 sm:py-16 lg:px-6">
            <h2 className="mb-6 text-center text-lg font-extrabold tracking-tight text-gray-800 dark:text-white lg:mb-8 lg:text-3xl">
                자주 묻는 질문
            </h2>
            <div className="mx-auto max-w-screen-md break-all">
                <Accordion flush collapseAll={true}>
                    <Accordion.Panel>
                        <Accordion.Title className="bg-transparent dark:bg-transparent">
                            <p className="mr-2">상대방이 프로필 조회 사실을 알 수 있나요?</p>
                        </Accordion.Title>
                        <Accordion.Content>
                            <p className="mb-2 text-gray-800 dark:text-white">
                                상대방은 절대로 조회 사실을 알 수 없습니다.
                            </p>
                        </Accordion.Content>
                    </Accordion.Panel>
                    <Accordion.Panel>
                        <Accordion.Title className="bg-transparent dark:bg-transparent">
                            <p className="mr-2">멀티프로필 조회도 가능한가요?</p>
                        </Accordion.Title>
                        <Accordion.Content>
                            <p className="mb-2 text-gray-800 dark:text-white">
                                특정 대상으로 설정된 멀티프로필은 조회할 수 없습니다.
                            </p>
                            <p className="mb-2 text-gray-800 dark:text-white">
                                그러나 상대방의 기본 프로필을 조회하여 자신이 멀티프로필이 적용된 상태인지 확인할 수
                                있습니다.
                            </p>
                        </Accordion.Content>
                    </Accordion.Panel>
                    <Accordion.Panel>
                        <Accordion.Title className="bg-transparent dark:bg-transparent">
                            <p className="mr-2">프로필 조회가 안되는 경우도 있나요?</p>
                        </Accordion.Title>
                        <Accordion.Content>
                        <p className="mb-2 text-gray-800 dark:text-white">
                                상대방의 "전화번호로 친구 추가 허용" 옵션이 비활성화 상태면 조회가 불가능합니다.
                            </p>
                            <p className="mb-2 text-gray-800 dark:text-white">
                                또한 카카오톡에 가입되지 않았거나 존재하지 않은 전화번호인 경우에도 조회가 불가능합니다.
                            </p>
                        </Accordion.Content>
                    </Accordion.Panel>
                    <Accordion.Panel>
                        <Accordion.Title className="bg-transparent dark:bg-transparent">
                            <p className="mr-2">서비스가 유료인 이유는 무엇인가요?</p>
                        </Accordion.Title>
                        <Accordion.Content>
                            <p className="mb-2 text-gray-800 dark:text-white">
                                개인적인 이윤을 추구하려는게 아닌, 사용자분들에게 더 나은 서비스를 지속적으로 제공하기
                                위해 발생하는 최소한의 서버 유지 비용을 도움받고자이니 양해 부탁드립니다.😊
                            </p>
                        </Accordion.Content>
                    </Accordion.Panel>
                </Accordion>
            </div>
        </div>
    );
};

export default Faq;
